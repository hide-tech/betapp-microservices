package com.yazykov.sportbet.paymentservice.dto;

import com.yazykov.sportbet.paymentservice.domain.CreditCard;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceDto {
    private String invoiceId;
    private CreditCardDto creditCard;
    private BigDecimal amount;
    private Long customerId;
    private LocalDateTime operationDateTime;
}
