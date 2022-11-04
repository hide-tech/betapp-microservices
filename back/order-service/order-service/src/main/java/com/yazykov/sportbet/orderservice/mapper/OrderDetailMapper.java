package com.yazykov.sportbet.orderservice.mapper;

import com.yazykov.sportbet.orderservice.domain.OrderDetail;
import com.yazykov.sportbet.orderservice.dto.OrderDetailDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderDetailMapper {

    OrderDetail orderDetailDtoToOrderDetail(OrderDetailDto orderDetailDto);

    OrderDetailDto orderDetailToOrderDetailDto(OrderDetail orderDetail);
}
