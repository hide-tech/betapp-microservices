package com.yazykov.sportbet.orderservice.service;

import com.yazykov.sportbet.orderservice.domain.Order;
import com.yazykov.sportbet.orderservice.domain.OrderDetail;
import com.yazykov.sportbet.orderservice.domain.OrderStatus;
import com.yazykov.sportbet.orderservice.dto.OrderDetailDto;
import com.yazykov.sportbet.orderservice.dto.OrderDto;
import com.yazykov.sportbet.orderservice.exception.OrderNotFoundException;
import com.yazykov.sportbet.orderservice.mapper.OrderMapper;
import com.yazykov.sportbet.orderservice.repository.OrderRepository;
import com.yazykov.sportbet.orderservice.saga.SagaOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final SagaOrchestrator sagaOrchestrator;

    public List<OrderDto> getAllOrdersByCustomerId(Long customerId){
        return orderRepository.findAllByCustomerId(customerId).stream().map(orderMapper::orderToOrderDto)
                .collect(Collectors.toList());
    }

    public OrderDto getOrderById(String orderId) throws OrderNotFoundException {
        Order order = orderRepository.findByOrderId(orderId).orElseThrow(()->
                new OrderNotFoundException("Order not found"));
        return orderMapper.orderToOrderDto(order);
    }

    public OrderDto createNewOrder(OrderDetailDto orderDetailDto){
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setEventId(orderDetailDto.getEventId());
        orderDetail.setEventDateTime(orderDetailDto.getEventDateTime());
        orderDetail.setCustomerId(orderDetailDto.getCustomerId());
        orderDetail.setBet(orderDetailDto.getBet());
        orderDetail.setAmount(orderDetailDto.getAmount());
        orderDetail.setOrderId(UUID.randomUUID().toString());
        Order order = new Order();
        order.setOrderDetail(orderDetail);
        order.setStatus(OrderStatus.CREATED);
        Order newOrder = orderRepository.save(order);
        sagaOrchestrator.sagaStart(newOrder.getOrderDetail());
        return orderMapper.orderToOrderDto(order);
    }
}
