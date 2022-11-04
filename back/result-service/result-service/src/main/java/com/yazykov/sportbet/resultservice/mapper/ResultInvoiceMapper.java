package com.yazykov.sportbet.resultservice.mapper;

import com.yazykov.sportbet.resultservice.domain.ResultInvoice;
import com.yazykov.sportbet.resultservice.dto.ResultInvoiceDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ResultInvoiceMapper {

    ResultInvoice resultInvoiceDtoToResultInvoice(ResultInvoiceDto resultInvoiceDto);

    ResultInvoiceDto resultInvoiceToResultInvoiceDto(ResultInvoice resultInvoice);
}
