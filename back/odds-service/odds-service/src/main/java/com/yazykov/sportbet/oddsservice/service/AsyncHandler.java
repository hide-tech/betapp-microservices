package com.yazykov.sportbet.oddsservice.service;

import com.yazykov.sportbet.oddsservice.dto.OddDto;
import com.yazykov.sportbet.oddsservice.dto.event.FaultResponseOdd;
import com.yazykov.sportbet.oddsservice.dto.event.OrderInfoOdd;
import com.yazykov.sportbet.oddsservice.dto.event.SuccessResponseOdd;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AsyncHandler {

    private final OddService oddService;
    private final QueueMessagingTemplate queueMessagingTemplate;
    @Value("${cloud.aws.end-point.url-send}")
    private String endpoint;

    @SqsListener(value = "OddQueue")
    public void proceed(OrderInfoOdd orderInfoOdd){
        String eventId = orderInfoOdd.getEventId();
        OddDto oddDto = oddService.findOddByEventId(eventId);
        if (oddDto==null) {
            FaultResponseOdd fr = new FaultResponseOdd(orderInfoOdd.getOrderId(), "Couldn't find odd");
            Message<FaultResponseOdd> payload = MessageBuilder.withPayload(fr)
                    .setHeader("message-group-id", orderInfoOdd.getOrderId())
                    .setHeader("message-deduplication-id", orderInfoOdd.getOrderId() + "odd")
                    .build();
            queueMessagingTemplate.send(endpoint, payload);
        } else {
            SuccessResponseOdd sr = new SuccessResponseOdd(orderInfoOdd.getOrderId(), oddDto);
            Message<SuccessResponseOdd> message = MessageBuilder.withPayload(sr)
                    .setHeader("message-group-id", orderInfoOdd.getOrderId())
                    .setHeader("message-deduplication-id", orderInfoOdd.getOrderId() + "odd")
                    .build();
            queueMessagingTemplate.send(endpoint,message);
        }
    }
}
