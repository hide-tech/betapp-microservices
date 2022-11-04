package com.yazykov.sportbet.orderservice.mapper;

import com.yazykov.sportbet.orderservice.domain.Order;
import com.yazykov.sportbet.orderservice.dto.OrderDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    Order orderDtoToOrder(OrderDto orderDto);

    OrderDto orderToOrderDto(Order order);
}
