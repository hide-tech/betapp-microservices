package com.yazykov.sportbet.resultservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OpenApiHandler {

    private final ResultInvoiceService resultInvoiceService;

    //here must be webClient to open bet api, but I insert random function for simplicity

    @Scheduled(fixedRate = 3000000)
    public void proceedResult(){
        resultInvoiceService.processResult();
    }
}
