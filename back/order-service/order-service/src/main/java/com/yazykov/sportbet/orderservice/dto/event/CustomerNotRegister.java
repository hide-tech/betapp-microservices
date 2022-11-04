package com.yazykov.sportbet.orderservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerNotRegister {
    private Long customerId;
    private String orderId;
    private String reason;
}
