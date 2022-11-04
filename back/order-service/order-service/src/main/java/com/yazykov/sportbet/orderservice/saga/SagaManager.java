package com.yazykov.sportbet.orderservice.saga;

import com.yazykov.sportbet.orderservice.domain.Order;
import com.yazykov.sportbet.orderservice.domain.OrderDetail;
import com.yazykov.sportbet.orderservice.domain.OrderStatus;
import com.yazykov.sportbet.orderservice.dto.OddDto;
import com.yazykov.sportbet.orderservice.dto.OrderDetailDto;
import com.yazykov.sportbet.orderservice.dto.event.*;
import com.yazykov.sportbet.orderservice.mapper.CreditCardMapper;
import com.yazykov.sportbet.orderservice.mapper.OrderDetailMapper;
import com.yazykov.sportbet.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class SagaManager {

    private final QueueMessagingTemplate queueMessagingTemplate;
    private final CreditCardMapper creditCardMapper;
    private final OrderRepository orderRepository;
    private final OrderDetailMapper orderDetailMapper;
    @Value("${cloud.aws.end-points.url-send-customer}")
    private String endpointCustomer;
    @Value("${cloud.aws.end-points.url-send-odd}")
    private String endpointOdd;
    @Value("${cloud.aws.end-points.url-send-payment}")
    private String endpointPayment;
    @Value("${cloud.aws.end-points.url-send-result}")
    private String endpointResult;

    public String startOrderSaga(OrderDetail orderDetail){
        OrderInfoOdd oio = new OrderInfoOdd(orderDetail.getOrderId(),
                orderDetail.getEventId(), orderDetail.getCustomerId());
        Message<OrderInfoOdd> message = MessageBuilder.withPayload(oio)
                .setHeader("message-group-id", orderDetail.getCustomerId())
                .setHeader("message-deduplication-id", orderDetail.getOrderId())
                .build();
        queueMessagingTemplate.send(endpointOdd, message);
        return "Successfully order created";
    }

    @Transactional
    public String processSuccessfulOddResponse(SuccessResponseOdd sro){
        String orderId = sro.getOrderId();
        Order order = orderRepository.findByOrderId(orderId).orElseThrow(()->
                new UnsupportedOperationException("Wrong order id"));
        order.setStatus(OrderStatus.ODD_APPROVED);
        OddDto odd = sro.getOdd();
        Order.setOddToOrderDetail(odd, order);
        orderRepository.save(order);
        OrderInfo oi = new OrderInfo(order.getOrderDetail().getCustomerId(),
                order.getOrderDetail().getCardId(), order.getOrderDetail().getOrderId());
        Message<OrderInfo> message = MessageBuilder.withPayload(oi)
                .setHeader("message-group-id", order.getOrderDetail().getCustomerId())
                .setHeader("message-deduplication-id", order.getOrderDetail().getOrderId())
                .build();
        queueMessagingTemplate.send(endpointCustomer, message);
        return "Successfully Approved by odd-service";
    }

    @Transactional
    public String processCustomerPaymentInfo(CustomerPaymentInfo cpi){
        String orderId = cpi.getOrderId();
        Order order = orderRepository.findByOrderId(orderId).orElseThrow(()->
                new UnsupportedOperationException("Wrong order id"));
        order.setStatus(OrderStatus.CUSTOMER_APPROVED);
        OrderDetail orderDetail = order.getOrderDetail();
        orderDetail.setCardId(cpi.getCreditCard().getId());
        order.setOrderDetail(orderDetail);
        orderRepository.save(order);
        InfoFromOrderService ifos = new InfoFromOrderService(orderId, cpi.getCustomerId(),
                creditCardMapper.credirCardApiDtoToCreditCardDto(cpi.getCreditCard()),
                order.getOrderDetail().getAmount());
        Message<InfoFromOrderService> message = MessageBuilder.withPayload(ifos)
                .setHeader("message-group-id", order.getOrderDetail().getCustomerId())
                .setHeader("message-deduplication-id", order.getOrderDetail().getOrderId())
                .build();
        queueMessagingTemplate.send(endpointPayment, message);
        return "Successfully Approved by customer-service";
    }

    @Transactional
    public String processSuccessfulResponse(SuccessfulResponse sr){
        String orderId = sr.getOrderId();
        Order order = orderRepository.findByOrderId(orderId).orElseThrow(()->
                new UnsupportedOperationException("Wrong order id"));
        order.setStatus(OrderStatus.PAYMENT_APPROVED);
        orderRepository.save(order);
        OrderDetailDto odd = orderDetailMapper.orderDetailToOrderDetailDto(order.getOrderDetail());
        Message<OrderDetailDto> message = MessageBuilder.withPayload(odd)
                .setHeader("message-group-id", order.getOrderDetail().getCustomerId())
                .setHeader("message-deduplication-id", order.getOrderDetail().getOrderId())
                .build();
        queueMessagingTemplate.send(endpointResult, message);
        return "Successfully Approved by payment-service";
    }

    @Transactional
    public String processSuccessResponseResult(SuccessResponseResult srr){
        String orderId = srr.getOrderId();
        Order order = orderRepository.findByOrderId(orderId).orElseThrow(()->
                new UnsupportedOperationException("Wrong order id"));
        order.setStatus(OrderStatus.RESULT_SENDED);
        orderRepository.save(order);
        return "Successfully accepted by result service";
    }

    @Transactional
    public String processOrderFinished(OrderFinish of){
        String orderId = of.getOrderId();
        Order order = orderRepository.findByOrderId(orderId).orElseThrow(()->
                new UnsupportedOperationException("Wrong order id"));
        order.setStatus(OrderStatus.FINISHED);
        orderRepository.save(order);
        return "Successfully order finished";
    }

    @Transactional
    public String processCustomerNotRegisterEvent(CustomerNotRegister cnr){
        String orderId = cnr.getOrderId();
        Order order = orderRepository.findByOrderId(orderId).orElseThrow(()->
                new UnsupportedOperationException("Wrong order id"));
        order.setStatus(OrderStatus.DECLINED);
        orderRepository.save(order);
        return cnr.getReason();
    }

    @Transactional
    public String processFaultResponse(FaultResponse fr){
        String orderId = fr.getOrderId();
        Order order = orderRepository.findByOrderId(orderId).orElseThrow(()->
                new UnsupportedOperationException("Wrong order id"));
        order.setStatus(OrderStatus.DECLINED);
        orderRepository.save(order);
        return fr.getReason();
    }

    @Transactional
    public String processFaultResponseOdd(FaultResponseOdd frd){
        String orderId = frd.getOrderId();
        Order order = orderRepository.findByOrderId(orderId).orElseThrow(()->
                new UnsupportedOperationException("Wrong order id"));
        order.setStatus(OrderStatus.DECLINED);
        orderRepository.save(order);
        return frd.getReason();
    }
}
