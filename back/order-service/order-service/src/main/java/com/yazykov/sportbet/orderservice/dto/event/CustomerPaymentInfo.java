package com.yazykov.sportbet.orderservice.dto.event;

import com.yazykov.sportbet.orderservice.dto.CreditCardApiDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerPaymentInfo {
    private String orderId;
    private Long customerId;
    private CreditCardApiDto creditCard;
}
