package com.yazykov.sportbet.oddsservice.dto.event;

import com.yazykov.sportbet.oddsservice.dto.OddDto;
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
