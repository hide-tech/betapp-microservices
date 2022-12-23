package com.yazykov.sportbet.oddsservice.service;

import com.yazykov.sportbet.oddsservice.dto.event.OrderInfoOdd;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AsyncHandler {

    private final OddService oddService;

    @SqsListener(value = "OddQueue")
    public void process(OrderInfoOdd orderInfoOdd){
        oddService.process(orderInfoOdd);
    }
}
