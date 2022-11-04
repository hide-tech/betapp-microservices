package com.yazykov.sportbet.oddsservice.service;

import com.yazykov.sportbet.oddsservice.dto.OddDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OpenApiOddService {

    private final OddService oddService;

    //here must be webclient for communication with open api, but for simplicity I write http endpoint

    @PostMapping("/odds/add")
    public ResponseEntity<?> addOdd(@RequestBody List<OddDto> oddDto){
        oddDto.forEach(oddService::addOdd);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
