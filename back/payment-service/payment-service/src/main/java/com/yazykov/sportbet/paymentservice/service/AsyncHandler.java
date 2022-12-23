package com.yazykov.sportbet.paymentservice.service;

import com.yazykov.sportbet.paymentservice.dto.event.InfoFromOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AsyncHandler {

    private final InvoiceService service;

    @SqsListener(value = "PaymentQueue")
    public void proceed(InfoFromOrderService info){
        service.process(info);
    }
}
