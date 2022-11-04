package com.yazykov.sportbet.oddsservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderInfoOdd {

    private String orderId;
    private String eventId;
    private Long customerId;
}
