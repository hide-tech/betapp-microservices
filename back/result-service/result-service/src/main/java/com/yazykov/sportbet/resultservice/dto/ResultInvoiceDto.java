package com.yazykov.sportbet.resultservice.dto;

import com.yazykov.sportbet.resultservice.domain.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResultInvoiceDto {
    private Long id;
    private OrderDetailDto orderDetail;
    private LocalDateTime checkTime;
    private Status status;
}
