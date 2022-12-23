package com.yazykov.sportbet.oddsservice.service;

import com.yazykov.sportbet.oddsservice.config.AWSParameters;
import com.yazykov.sportbet.oddsservice.domain.Odd;
import com.yazykov.sportbet.oddsservice.dto.OddDto;
import com.yazykov.sportbet.oddsservice.dto.event.FaultResponseOdd;
import com.yazykov.sportbet.oddsservice.dto.event.OrderInfoOdd;
import com.yazykov.sportbet.oddsservice.dto.event.SuccessResponseOdd;
import com.yazykov.sportbet.oddsservice.mapper.OddMapper;
import com.yazykov.sportbet.oddsservice.repository.OddRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OddService {

    private final OddRepository oddRepository;
    private final OddMapper oddMapper;
    private final QueueMessagingTemplate queueMessagingTemplate;
    private final AWSParameters parameters;

    public OddDto findOddByEventId(String eventId){
        return oddMapper.oddToOddDto(oddRepository.findByEventId(eventId));
    }

    public void addOdd(OddDto oddDto){
        Odd odd = oddMapper.oddDtoToOdd(oddDto);
        oddRepository.save(odd);
    }

    public void process(OrderInfoOdd orderInfoOdd){
        String eventId = orderInfoOdd.getEventId();
        OddDto oddDto = findOddByEventId(eventId);
        if (oddDto==null) {
            FaultResponseOdd fr = new FaultResponseOdd(orderInfoOdd.getOrderId(), "Couldn't find odd");
            Message<FaultResponseOdd> payload = MessageBuilder.withPayload(fr)
                    .setHeader("message-group-id", "OrderQueue")
                    .setHeader("message-deduplication-id", orderInfoOdd.getOrderId() + "odd")
                    .build();
            queueMessagingTemplate.send(parameters.distinctQueue(), payload);
        } else {
            SuccessResponseOdd sr = new SuccessResponseOdd(orderInfoOdd.getOrderId(), oddDto);
            Message<SuccessResponseOdd> message = MessageBuilder.withPayload(sr)
                    .setHeader("message-group-id", "OrderQueue")
                    .setHeader("message-deduplication-id", orderInfoOdd.getOrderId() + "odd")
                    .build();
            queueMessagingTemplate.send(parameters.distinctQueue(),message);
        }
    }
}
