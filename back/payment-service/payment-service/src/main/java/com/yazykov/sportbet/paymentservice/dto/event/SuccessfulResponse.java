package com.yazykov.sportbet.paymentservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SuccessfulResponse {

    private String invoiceId;
    private String orderId;
}
