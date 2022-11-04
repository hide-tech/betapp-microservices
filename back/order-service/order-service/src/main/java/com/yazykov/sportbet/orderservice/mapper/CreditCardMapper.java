package com.yazykov.sportbet.orderservice.mapper;

import com.yazykov.sportbet.orderservice.dto.CreditCardApiDto;
import com.yazykov.sportbet.orderservice.dto.CreditCardDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CreditCardMapper {

    CreditCardDto credirCardApiDtoToCreditCardDto(CreditCardApiDto creditCardApiDto);

    CreditCardApiDto creditCardDtoToCreditCardApiDto(CreditCardDto creditCardDto);
}
