package com.yazykov.sportbet.resultservice.service;

import com.yazykov.sportbet.resultservice.config.AWSParameters;
import com.yazykov.sportbet.resultservice.domain.ResultInvoice;
import com.yazykov.sportbet.resultservice.domain.Status;
import com.yazykov.sportbet.resultservice.dto.OrderDetailDto;
import com.yazykov.sportbet.resultservice.dto.ResultInvoiceDto;
import com.yazykov.sportbet.resultservice.dto.event.OrderFinish;
import com.yazykov.sportbet.resultservice.dto.event.SuccessResponseResult;
import com.yazykov.sportbet.resultservice.exception.ResultNotFoundException;
import com.yazykov.sportbet.resultservice.mapper.OrderDetailMapper;
import com.yazykov.sportbet.resultservice.mapper.ResultInvoiceMapper;
import com.yazykov.sportbet.resultservice.repository.ResultInvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResultInvoiceService {

    private final ResultInvoiceRepository resultInvoiceRepository;
    private final ResultInvoiceMapper resultInvoiceMapper;
    private final OrderDetailMapper orderDetailMapper;
    private final QueueMessagingTemplate queueMessagingTemplate;
    private final AWSParameters parameters;

    public ResultInvoiceDto getResultByOrderId(String orderId) throws ResultNotFoundException {
        ResultInvoice resultInvoice = resultInvoiceRepository.findByOrderId(orderId).orElseThrow(()->
                new ResultNotFoundException("Result invoice not found"));
        return resultInvoiceMapper.resultInvoiceToResultInvoiceDto(resultInvoice);
    }

    public List<ResultInvoice> getPendingResult(LocalDateTime check){
        return resultInvoiceRepository.findAllPending(check);
    }

    public void changeStatus(Long resultId, Status status) throws ResultNotFoundException {
        ResultInvoice resultInvoice = resultInvoiceRepository.findById(resultId).orElseThrow(()->
                new ResultNotFoundException("Result invoice not found"));
        resultInvoice.setStatus(status);
        resultInvoiceRepository.save(resultInvoice);
    }

    public ResultInvoice saveResult(ResultInvoice resultInvoice){
        return resultInvoiceRepository.save(resultInvoice);
    }

    public List<ResultInvoice> getWonPendingResult(LocalDateTime check) {
        return resultInvoiceRepository.findAllWonPending(check);
    }

    public List<ResultInvoice> getWonErrorResult(LocalDateTime check) {
        return resultInvoiceRepository.findAllWonErrorPending(check);
    }

    public void processAsync(OrderDetailDto orderDetailDto){
        ResultInvoice resultInvoice = new ResultInvoice();
        resultInvoice.setOrderDetail(orderDetailMapper.orderDetailDtoToOrderDetail(orderDetailDto));
        resultInvoice.setStatus(Status.PENDING);
        resultInvoice.setCheckTime(orderDetailDto.getEventDateTime().plusHours(4L));
        ResultInvoice newResultInvoice = saveResult(resultInvoice);
        SuccessResponseResult srr = new SuccessResponseResult(orderDetailDto.getOrderId(), newResultInvoice.getId());
        Message<SuccessResponseResult> message = MessageBuilder.withPayload(srr)
                .setHeader("message-group-id", "OrderQueue")
                .setHeader("message-deduplication-id", orderDetailDto.getOrderId() + "result")
                .build();
        queueMessagingTemplate.send(parameters.distinctQueue(), message);
    }

    public void processResult(){
        LocalDateTime now = LocalDateTime.now();
        List<ResultInvoice> results = getPendingResult(now);
        results.forEach(res -> {
            String eventId = res.getOrderDetail().getEventId();
            boolean betResult = webClientCheck(eventId);
            if (betResult){
                try {
                    changeStatus(res.getId(), Status.WIN_PAY_PENDING);
                } catch (ResultNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    changeStatus(res.getId(), Status.LOST);
                } catch (ResultNotFoundException e) {
                    e.printStackTrace();
                }
                OrderFinish of = new OrderFinish(res.getOrderDetail().getOrderId(), Status.LOST);
                Message<OrderFinish> message = MessageBuilder.withPayload(of)
                        .setHeader("message-group-id", "OrderQueue")
                        .setHeader("message-deduplication-id", res.getOrderDetail().getOrderId() + "decline")
                        .build();
                queueMessagingTemplate.send(parameters.distinctQueue(), message);
            }
        });
    }

    private boolean webClientCheck(String eventId) {
        double v = Math.random() * 10;
        return v > 5L;
    }

    public void processPaying(){
        LocalDateTime now = LocalDateTime.now();
        List<ResultInvoice> results = getWonPendingResult(now);
        results.forEach(res -> {
            String eventId = res.getOrderDetail().getEventId();
            boolean betResult = webClientPay(eventId);
            if (!betResult){
                try {
                    changeStatus(res.getId(), Status.WIN_PAY_ERROR);
                } catch (ResultNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    changeStatus(res.getId(), Status.WIN_PAYED);
                } catch (ResultNotFoundException e) {
                    e.printStackTrace();
                }
                OrderFinish of = new OrderFinish(res.getOrderDetail().getOrderId(), Status.WON);
                Message<OrderFinish> message = MessageBuilder.withPayload(of)
                        .setHeader("message-group-id", "OrderQueue")
                        .setHeader("message-deduplication-id", res.getOrderDetail().getOrderId() + "finish")
                        .build();
                queueMessagingTemplate.send(parameters.distinctQueue(), message);
            }
        });
    }

    private boolean webClientPay(String eventId) {
        double v = Math.random() * 10;
        return v > 5L;
    }
}
