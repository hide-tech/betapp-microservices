package com.yazykov.sportbet.orderservice.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class OrderDetail {
    private String orderId;
    private Long customerId;
    private String eventId;
    private String bet;
    private BigDecimal odd;
    private BigDecimal amount;
    private LocalDateTime eventDateTime;
    private Long cardId;
}
