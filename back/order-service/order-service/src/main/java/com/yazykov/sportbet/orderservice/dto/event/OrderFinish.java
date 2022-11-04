package com.yazykov.sportbet.orderservice.dto.event;

import com.yazykov.sportbet.orderservice.dto.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderFinish {

    private String orderId;
    private Status status;
}
