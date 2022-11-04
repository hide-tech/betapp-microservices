package com.yazykov.sportbet.resultservice.controller;

import com.yazykov.sportbet.resultservice.exception.ResultNotFoundException;
import com.yazykov.sportbet.resultservice.service.ResultInvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class ResultController {

    private final ResultInvoiceService resultInvoiceService;

    @GetMapping("/results/error")
    public ResponseEntity<?> getAllErrorPayingResults(){
        return ResponseEntity.ok(resultInvoiceService.getWonErrorResult(LocalDateTime.now()));
    }

    @GetMapping("/results/{orderId}")
    public ResponseEntity<?> getResultByOrderId(@PathVariable("orderId") String orderId)
            throws ResultNotFoundException {
        return ResponseEntity.ok(resultInvoiceService.getResultByOrderId(orderId));
    }
}
