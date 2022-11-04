package com.yazykov.sportbet.customerservice.mapper;

import com.yazykov.sportbet.customerservice.domain.CreditCard;
import com.yazykov.sportbet.customerservice.dto.CreditCardApiDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CreditCardMapper {

    CreditCardApiDto creditCardToCreditCardApiDto(CreditCard creditCard);

    CreditCard creditCardApiDtoToCreditCard(CreditCardApiDto creditCardApiDto);
}
