package com.yazykov.sportbet.oddsservice.mapper;

import com.yazykov.sportbet.oddsservice.domain.Odd;
import com.yazykov.sportbet.oddsservice.dto.OddDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OddMapper {

    Odd oddDtoToOdd(OddDto oddDto);

    OddDto oddToOddDto(Odd odd);
}
