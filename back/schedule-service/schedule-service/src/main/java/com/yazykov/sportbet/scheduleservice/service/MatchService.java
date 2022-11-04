package com.yazykov.sportbet.scheduleservice.service;

import com.yazykov.sportbet.scheduleservice.domain.CompetitionMatch;
import com.yazykov.sportbet.scheduleservice.dto.MatchDto;
import com.yazykov.sportbet.scheduleservice.mapper.MatchMapper;
import com.yazykov.sportbet.scheduleservice.repository.CompetitionMatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final CompetitionMatchRepository repository;
    private final MatchMapper mapper;

    public List<MatchDto> getMatches(int numberOfEvents, int page) {
        Pageable pageable = Pageable.ofSize(numberOfEvents).withPage(page);
        return repository.findAll(pageable).stream()
                .map(mapper::competitionMatchToMatchDto).collect(Collectors.toList());
    }

    public void addEvent(MatchDto matchDto){
        CompetitionMatch match = mapper.matchDtoToCompetitionMatch(matchDto);
        repository.save(match);
    }
}
