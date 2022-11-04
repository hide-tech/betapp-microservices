package com.yazykov.sportbet.orderservice.dto;

import com.yazykov.sportbet.orderservice.domain.OrderDetail;
import com.yazykov.sportbet.orderservice.domain.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private Long id;
    private OrderStatus status;
    private OrderDetail orderDetail;
}
