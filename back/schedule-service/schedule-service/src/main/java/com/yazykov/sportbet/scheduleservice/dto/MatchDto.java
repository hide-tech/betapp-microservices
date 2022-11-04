package com.yazykov.sportbet.scheduleservice.dto;

import com.yazykov.sportbet.scheduleservice.domain.CompetitionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatchDto {
    private Long id;
    private String eventId;
    private CompetitionType type;
    private LocalDateTime dateTime;
    private String description;
}
