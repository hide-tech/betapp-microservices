package com.yazykov.sportbet.paymentservice.dto;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreditCardDto {
    private Long id;
    private String name;
    private String cardNumber;
    private String terminateDate;
    private String holderName;
    private String cvvNumber;
}
