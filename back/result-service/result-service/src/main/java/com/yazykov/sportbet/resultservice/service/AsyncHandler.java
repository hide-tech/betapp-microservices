package com.yazykov.sportbet.resultservice.service;

import com.yazykov.sportbet.resultservice.domain.ResultInvoice;
import com.yazykov.sportbet.resultservice.domain.Status;
import com.yazykov.sportbet.resultservice.dto.OrderDetailDto;
import com.yazykov.sportbet.resultservice.dto.event.SuccessResponseResult;
import com.yazykov.sportbet.resultservice.mapper.OrderDetailMapper;
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

    private final ResultInvoiceService resultInvoiceService;
    private final OrderDetailMapper orderDetailMapper;
    private final QueueMessagingTemplate queueMessagingTemplate;
    @Value("${cloud.aws.end-point.url-send}")
    private String endpoint;

    @SqsListener(value = "ResultQueue")
    public void proceed(OrderDetailDto orderDetailDto){
        ResultInvoice resultInvoice = new ResultInvoice();
        resultInvoice.setOrderDetail(orderDetailMapper.orderDetailDtoToOrderDetail(orderDetailDto));
        resultInvoice.setStatus(Status.PENDING);
        resultInvoice.setCheckTime(orderDetailDto.getEventDateTime().plusHours(4L));
        ResultInvoice newResultInvoice = resultInvoiceService.saveResult(resultInvoice);
        SuccessResponseResult srr = new SuccessResponseResult(orderDetailDto.getOrderId(), newResultInvoice.getId());
        Message<SuccessResponseResult> message = MessageBuilder.withPayload(srr)
                .setHeader("message-group-id", orderDetailDto.getOrderId())
                .setHeader("message-deduplication-id", orderDetailDto.getOrderId() + "result")
                .build();
        queueMessagingTemplate.send(endpoint, message);
    }
}
