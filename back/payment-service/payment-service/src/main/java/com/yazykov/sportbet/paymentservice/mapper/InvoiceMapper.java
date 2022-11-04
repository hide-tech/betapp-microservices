package com.yazykov.sportbet.paymentservice.mapper;

import com.yazykov.sportbet.paymentservice.domain.Invoice;
import com.yazykov.sportbet.paymentservice.dto.InvoiceDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {

    Invoice invoiceDtoToInvoice(InvoiceDto invoiceDto);

    InvoiceDto invoiceToInvoiceDto(Invoice invoice);
}
