package com.yazykov.sportbet.customerservice.service;

import com.yazykov.sportbet.customerservice.dto.event.OrderInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AsyncHandler {

    private final CustomerService service;

    @SqsListener(value = "CustomerQueue")
    private void process(OrderInfo orderInfo){
        service.process(orderInfo);
    }
}
