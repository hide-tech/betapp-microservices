package com.yazykov.sportbet.resultservice.controller;

import com.yazykov.sportbet.resultservice.exception.ErrorDetails;
import com.yazykov.sportbet.resultservice.exception.ResultNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Date;

@RestControllerAdvice
public class ControllerAdviceRest {

    @ExceptionHandler(ResultNotFoundException.class)
    public ResponseEntity<?> handleResultNotFoundException(ResultNotFoundException ex) {
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setTitle("change card id");
        errorDetails.setDetail(ex.getMessage());
        errorDetails.setDevMessage(ex.getClass().getName());
        errorDetails.setTimeStamp(new Date().getTime());
        errorDetails.setStatus(HttpStatus.BAD_REQUEST.value());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
    }
}
