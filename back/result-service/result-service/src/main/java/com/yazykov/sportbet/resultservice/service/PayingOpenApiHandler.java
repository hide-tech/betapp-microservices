package com.yazykov.sportbet.resultservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PayingOpenApiHandler {

    private final ResultInvoiceService resultInvoiceService;

    //here must be webClient to open pay api, but I insert random function for simplicity

    @Scheduled(fixedRate = 3000000)
    public void proceedPayingResult(){
       resultInvoiceService.processPaying();
    }
}
