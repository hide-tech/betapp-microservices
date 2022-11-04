package com.yazykov.sportbet.scheduleservice.mapper;

import com.yazykov.sportbet.scheduleservice.domain.CompetitionMatch;
import com.yazykov.sportbet.scheduleservice.dto.MatchDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MatchMapper {

    CompetitionMatch matchDtoToCompetitionMatch(MatchDto matchDto);

    MatchDto competitionMatchToMatchDto(CompetitionMatch competitionMatch);
}
