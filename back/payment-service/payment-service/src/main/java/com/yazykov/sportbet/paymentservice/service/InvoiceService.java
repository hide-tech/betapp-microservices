package com.yazykov.sportbet.paymentservice.service;

import com.yazykov.sportbet.paymentservice.domain.Invoice;
import com.yazykov.sportbet.paymentservice.dto.InvoiceDto;
import com.yazykov.sportbet.paymentservice.mapper.InvoiceMapper;
import com.yazykov.sportbet.paymentservice.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper;

    public InvoiceDto getInvoiceById(String invoiceId){
        Invoice invoice = invoiceRepository.getById(invoiceId);
        return invoiceMapper.invoiceToInvoiceDto(invoice);
    }

    public Invoice saveNewInvoice(Invoice invoice){
        return invoiceRepository.save(invoice);
    }
}
