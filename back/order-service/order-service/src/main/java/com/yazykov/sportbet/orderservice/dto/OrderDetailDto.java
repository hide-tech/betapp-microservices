package com.yazykov.sportbet.orderservice.dto;

import com.yazykov.sportbet.orderservice.domain.OrderDetail;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailDto {
    private String orderId;
    private Long customerId;
    private String eventId;
    private String bet;
    private BigDecimal odd;
    private BigDecimal amount;
    private LocalDateTime eventDateTime;
    private Long cardId;
}
