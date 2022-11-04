package com.yazykov.sportbet.paymentservice.domain;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DynamoDBDocument
public class CreditCard {
    @DynamoDBAttribute
    private Long id;
    @DynamoDBAttribute
    private String name;
    @DynamoDBAttribute
    private String cardNumber;
    @DynamoDBAttribute
    private String terminateDate;
    @DynamoDBAttribute
    private String holderName;
    @DynamoDBAttribute
    private String cvvNumber;
}
