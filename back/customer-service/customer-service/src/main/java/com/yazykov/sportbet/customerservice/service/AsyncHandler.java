package com.yazykov.sportbet.customerservice.service;

import com.yazykov.sportbet.customerservice.domain.CreditCard;
import com.yazykov.sportbet.customerservice.domain.Customer;
import com.yazykov.sportbet.customerservice.domain.Status;
import com.yazykov.sportbet.customerservice.dto.event.CustomerNotRegister;
import com.yazykov.sportbet.customerservice.dto.event.CustomerPaymentInfo;
import com.yazykov.sportbet.customerservice.dto.event.OrderInfo;
import com.yazykov.sportbet.customerservice.mapper.CreditCardMapper;
import com.yazykov.sportbet.customerservice.repository.CreditCardRepository;
import com.yazykov.sportbet.customerservice.repository.CustomerRpository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AsyncHandler {

    private final QueueMessagingTemplate queueMessagingTemplate;
    private final CustomerRpository customerRpository;
    private final CreditCardRepository creditCardRepository;
    private final CreditCardMapper creditCardMapper;
    @Value("${cloud.aws.end-point.url-send}")
    private String endpoint;

    @SqsListener(value = "CustomerQueue")
    private void procced(OrderInfo orderInfo){
        boolean fault = false;
        String reason = "";
        Optional<Customer> opt = customerRpository.findById(orderInfo.getCustomerId());
        if (opt.isEmpty()) {
            fault = true;
            reason = "customer with id not found";
        }
        Customer customer = opt.get();
        if (!customer.getStatus().equals(Status.ACTIVE)) {
            fault = true;
            reason = "customer has no ACTIVE status";
        }

        Optional<CreditCard> cardOpt = creditCardRepository.findById(orderInfo.getCardId());
        if (cardOpt.isEmpty()) {
            fault = true;
            reason = "customer's card with id not found";
        }
        CreditCard card = cardOpt.get();

        if (fault){
            CustomerNotRegister cnr = new CustomerNotRegister(orderInfo.getCustomerId(),
                    orderInfo.getOrderId(), reason);
            Message<CustomerNotRegister> payload = MessageBuilder.withPayload(cnr)
                    .setHeader("message-group-id", orderInfo.getOrderId())
                    .setHeader("message-deduplication-id", orderInfo.getOrderId() + "customer")
                    .build();
            queueMessagingTemplate.send(endpoint, payload);
        } else {
            CustomerPaymentInfo cpi = new CustomerPaymentInfo(orderInfo.getOrderId(), orderInfo.getCustomerId(),
                    creditCardMapper.creditCardToCreditCardApiDto(card));
            Message<CustomerPaymentInfo> message = MessageBuilder.withPayload(cpi)
                    .setHeader("message-group-id", orderInfo.getOrderId())
                    .setHeader("message-deduplication-id", orderInfo.getOrderId() + "customer")
                    .build();
            queueMessagingTemplate.send(endpoint, message);
        }
    }
}
