package com.yazykov.sportbet.customerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreditCardApiDto {
    private Long id;
    private String name;
    private String cardNumber;
    private String terminateDate;
    private String holderName;
    private String cvvNumber;
}
