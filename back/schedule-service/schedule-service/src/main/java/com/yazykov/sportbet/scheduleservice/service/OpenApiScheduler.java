package com.yazykov.sportbet.scheduleservice.service;

import com.yazykov.sportbet.scheduleservice.dto.MatchDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OpenApiScheduler {

    private final MatchService matchService;

    //here must be webclient for communication with open api, but for simplicity I write http endpoint

    @PostMapping("/events/add")
    public ResponseEntity<?> addEventIntoDatabase(@RequestBody List<MatchDto> matchDtos){
        matchDtos.forEach(matchService::addEvent);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
