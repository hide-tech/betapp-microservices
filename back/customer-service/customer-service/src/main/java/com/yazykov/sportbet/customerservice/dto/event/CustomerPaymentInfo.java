package com.yazykov.sportbet.customerservice.dto.event;

import com.yazykov.sportbet.customerservice.dto.CreditCardApiDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerPaymentInfo {
    private String orderId;
    private Long customerId;
    private CreditCardApiDto creditCard;
}
