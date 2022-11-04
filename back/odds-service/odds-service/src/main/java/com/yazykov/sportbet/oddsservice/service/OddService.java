package com.yazykov.sportbet.oddsservice.service;

import com.yazykov.sportbet.oddsservice.domain.Odd;
import com.yazykov.sportbet.oddsservice.dto.OddDto;
import com.yazykov.sportbet.oddsservice.mapper.OddMapper;
import com.yazykov.sportbet.oddsservice.repository.OddRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OddService {

    private final OddRepository oddRepository;
    private final OddMapper oddMapper;

    public OddDto findOddByEventId(String eventId){
        return oddMapper.oddToOddDto(oddRepository.findByEventId(eventId));
    }

    public void addOdd(OddDto oddDto){
        Odd odd = oddMapper.oddDtoToOdd(oddDto);
        oddRepository.save(odd);
    }
}
