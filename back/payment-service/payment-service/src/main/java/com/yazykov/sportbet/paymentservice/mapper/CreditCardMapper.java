package com.yazykov.sportbet.paymentservice.mapper;

import com.yazykov.sportbet.paymentservice.domain.CreditCard;
import com.yazykov.sportbet.paymentservice.dto.CreditCardDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CreditCardMapper {

    CreditCard creditCardDtoToCreditCard(CreditCardDto creditCardDto);

    CreditCardDto creditCardToCreditCardDto(CreditCard creditCard);
}
