package com.yazykov.sportbet.resultservice.service;

import com.yazykov.sportbet.resultservice.domain.ResultInvoice;
import com.yazykov.sportbet.resultservice.domain.Status;
import com.yazykov.sportbet.resultservice.dto.ResultInvoiceDto;
import com.yazykov.sportbet.resultservice.exception.ResultNotFoundException;
import com.yazykov.sportbet.resultservice.mapper.ResultInvoiceMapper;
import com.yazykov.sportbet.resultservice.repository.ResultInvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResultInvoiceService {

    private final ResultInvoiceRepository resultInvoiceRepository;
    private final ResultInvoiceMapper resultInvoiceMapper;

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
}
