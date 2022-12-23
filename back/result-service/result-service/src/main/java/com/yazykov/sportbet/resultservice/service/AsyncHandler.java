package com.yazykov.sportbet.resultservice.service;

import com.yazykov.sportbet.resultservice.dto.OrderDetailDto;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AsyncHandler {

    private final ResultInvoiceService resultInvoiceService;

    @SqsListener(value = "ResultQueue")
    public void process(OrderDetailDto orderDetailDto){
        resultInvoiceService.processAsync(orderDetailDto);
    }
}
