package com.yazykov.sportbet.oddsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OddDto {
    private Long id;
    private String eventId;
    private BigDecimal ratio;
    private BigDecimal backRatio;
    private LocalDateTime thresholdTime;
}
