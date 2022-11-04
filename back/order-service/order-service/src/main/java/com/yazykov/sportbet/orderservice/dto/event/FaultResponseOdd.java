package com.yazykov.sportbet.orderservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FaultResponseOdd {

    private String orderId;
    private String reason;
}
