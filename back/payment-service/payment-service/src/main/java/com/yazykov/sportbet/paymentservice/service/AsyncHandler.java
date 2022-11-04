package com.yazykov.sportbet.paymentservice.service;

import com.yazykov.sportbet.paymentservice.domain.CreditCard;
import com.yazykov.sportbet.paymentservice.domain.Invoice;
import com.yazykov.sportbet.paymentservice.dto.event.FaultResponse;
import com.yazykov.sportbet.paymentservice.dto.event.InfoFromOrderService;
import com.yazykov.sportbet.paymentservice.dto.event.SuccessfulResponse;
import com.yazykov.sportbet.paymentservice.mapper.CreditCardMapper;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AsyncHandler {

    private final InvoiceService invoiceService;
    private final StripeService stripeService;
    private final CreditCardMapper creditCardMapper;
    private final QueueMessagingTemplate queueMessagingTemplate;
    @Value("${cloud.aws.queue.send-endpoint}")
    private String endpoint;

    @SqsListener(value = "PaymentQueue")
    public void proceed(InfoFromOrderService info){
        CreditCard card = creditCardMapper.creditCardDtoToCreditCard(info.getCreditCard());
        BigDecimal amount = info.getAmount();
        boolean result = stripeService.pay(card, amount);
        if (result){
            Invoice invoice = new Invoice();
            invoice.setAmount(amount);
            invoice.setCreditCard(card);
            invoice.setCustomerId(info.getCustomerId());
            invoice.setOperationDateTime(LocalDateTime.now());
            Invoice newInvoice = invoiceService.saveNewInvoice(invoice);

            SuccessfulResponse sr = new SuccessfulResponse(newInvoice.getInvoiceId(), info.getOrderId());
            Message<SuccessfulResponse> payload = MessageBuilder.withPayload(sr)
                    .setHeader("message-group-id", info.getOrderId())
                    .setHeader("message-deduplication-id", info.getOrderId() + "payment")
                    .build();
            queueMessagingTemplate.send(endpoint, payload);
        } else {
            FaultResponse fr = new FaultResponse(info.getOrderId(), "payment fault");
            Message<FaultResponse> message = MessageBuilder.withPayload(fr)
                    .setHeader("message-group-id", info.getOrderId())
                    .setHeader("message-deduplication-id", info.getOrderId() + "payment")
                    .build();
            queueMessagingTemplate.send(endpoint, message);
        }
    }
}
