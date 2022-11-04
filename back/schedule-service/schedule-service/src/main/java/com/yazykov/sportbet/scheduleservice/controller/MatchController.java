package com.yazykov.sportbet.scheduleservice.controller;

import com.yazykov.sportbet.scheduleservice.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @GetMapping("/matches")
    public ResponseEntity<?> getAllMatches(@RequestParam("events") int numberOfEvents,
                                           @PathVariable("page") int page){
        return ResponseEntity.ok(matchService.getMatches(numberOfEvents, page));
    }
}
