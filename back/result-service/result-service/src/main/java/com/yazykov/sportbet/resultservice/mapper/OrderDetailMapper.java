package com.yazykov.sportbet.resultservice.mapper;

import com.yazykov.sportbet.resultservice.domain.OrderDetail;
import com.yazykov.sportbet.resultservice.dto.OrderDetailDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderDetailMapper {

    OrderDetail orderDetailDtoToOrderDetail(OrderDetailDto orderDetailDto);

    OrderDetailDto orderDetailToOrderDetailDto(OrderDetail orderDetail);
}
