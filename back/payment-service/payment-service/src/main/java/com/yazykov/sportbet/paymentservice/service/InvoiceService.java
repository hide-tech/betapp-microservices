package com.yazykov.sportbet.paymentservice.service;

import com.yazykov.sportbet.paymentservice.config.AWSParameters;
import com.yazykov.sportbet.paymentservice.domain.CreditCard;
import com.yazykov.sportbet.paymentservice.domain.Invoice;
import com.yazykov.sportbet.paymentservice.dto.InvoiceDto;
import com.yazykov.sportbet.paymentservice.dto.event.FaultResponse;
import com.yazykov.sportbet.paymentservice.dto.event.InfoFromOrderService;
import com.yazykov.sportbet.paymentservice.dto.event.SuccessfulResponse;
import com.yazykov.sportbet.paymentservice.mapper.CreditCardMapper;
import com.yazykov.sportbet.paymentservice.mapper.InvoiceMapper;
import com.yazykov.sportbet.paymentservice.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper;
    private final QueueMessagingTemplate queueMessagingTemplate;
    private final StripeService stripeService;
    private final AWSParameters parameters;
    private final CreditCardMapper creditCardMapper;

    public InvoiceDto getInvoiceById(String invoiceId){
        Invoice invoice = invoiceRepository.getById(invoiceId);
        return invoiceMapper.invoiceToInvoiceDto(invoice);
    }

    public Invoice saveNewInvoice(Invoice invoice){
        return invoiceRepository.save(invoice);
    }

    public void process(InfoFromOrderService info){
        CreditCard card = creditCardMapper.creditCardDtoToCreditCard(info.getCreditCard());
        BigDecimal amount = info.getAmount();
        boolean result = stripeService.pay(card, amount);
        if (result){
            Invoice invoice = new Invoice();
            invoice.setAmount(amount);
            invoice.setCreditCard(card);
            invoice.setCustomerId(info.getCustomerId());
            invoice.setOperationDateTime(LocalDateTime.now());
            Invoice newInvoice = saveNewInvoice(invoice);

            SuccessfulResponse sr = new SuccessfulResponse(newInvoice.getInvoiceId(), info.getOrderId());
            Message<SuccessfulResponse> payload = MessageBuilder.withPayload(sr)
                    .setHeader("message-group-id", "OrderQueue")
                    .setHeader("message-deduplication-id", info.getOrderId() + "payment")
                    .build();
            queueMessagingTemplate.send(parameters.distinctQueue(), payload);
        } else {
            FaultResponse fr = new FaultResponse(info.getOrderId(), "payment fault");
            Message<FaultResponse> message = MessageBuilder.withPayload(fr)
                    .setHeader("message-group-id", "OrderQueue")
                    .setHeader("message-deduplication-id", info.getOrderId() + "payment")
                    .build();
            queueMessagingTemplate.send(parameters.distinctQueue(), message);
        }
    }
}
