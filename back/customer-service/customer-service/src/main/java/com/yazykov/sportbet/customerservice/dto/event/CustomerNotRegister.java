package com.yazykov.sportbet.customerservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerNotRegister {
    private Long customerId;
    private String orderId;
    private String declineReason;
}
