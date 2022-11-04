package com.yazykov.sportbet.orderservice.dto.event;

import com.yazykov.sportbet.orderservice.dto.OddDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SuccessResponseOdd {

    private String orderId;
    private OddDto odd;
}
