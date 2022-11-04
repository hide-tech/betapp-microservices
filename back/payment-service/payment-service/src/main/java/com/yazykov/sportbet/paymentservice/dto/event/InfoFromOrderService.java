package com.yazykov.sportbet.paymentservice.dto.event;

import com.yazykov.sportbet.paymentservice.dto.CreditCardDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InfoFromOrderService {

    private String orderId;
    private Long customerId;
    private CreditCardDto creditCard;
    private BigDecimal amount;
}
