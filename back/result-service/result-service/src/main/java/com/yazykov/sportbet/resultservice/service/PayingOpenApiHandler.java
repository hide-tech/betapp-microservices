package com.yazykov.sportbet.resultservice.service;

import com.yazykov.sportbet.resultservice.domain.ResultInvoice;
import com.yazykov.sportbet.resultservice.domain.Status;
import com.yazykov.sportbet.resultservice.dto.event.OrderFinish;
import com.yazykov.sportbet.resultservice.exception.ResultNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PayingOpenApiHandler {

    private final ResultInvoiceService resultInvoiceService;
    private final QueueMessagingTemplate queueMessagingTemplate;
    @Value("${cloud.aws.end-point.url-send}")
    private String endpoint;

    //here must be webClient to open pay api, but I insert random function for simplicity

    @Scheduled(fixedRate = 3000000)
    public void proceedPayingResult(){
        LocalDateTime now = LocalDateTime.now();
        List<ResultInvoice> results = resultInvoiceService.getWonPendingResult(now);
        results.forEach(res -> {
            String eventId = res.getOrderDetail().getEventId();
            boolean betResult = webClientPay(eventId);
            if (!betResult){
                try {
                    resultInvoiceService.changeStatus(res.getId(), Status.WIN_PAY_ERROR);
                } catch (ResultNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    resultInvoiceService.changeStatus(res.getId(), Status.WIN_PAYED);
                } catch (ResultNotFoundException e) {
                    e.printStackTrace();
                }
                OrderFinish of = new OrderFinish(res.getOrderDetail().getOrderId(), Status.WON);
                Message<OrderFinish> message = MessageBuilder.withPayload(of)
                        .setHeader("message-group-id", res.getOrderDetail().getOrderId())
                        .setHeader("message-deduplication-id", res.getOrderDetail().getOrderId() + "finish")
                        .build();
                queueMessagingTemplate.send(endpoint, message);
            }
        });
    }

    private boolean webClientPay(String eventId) {
        double v = Math.random() * 10;
        return v > 5L;
    }
}
