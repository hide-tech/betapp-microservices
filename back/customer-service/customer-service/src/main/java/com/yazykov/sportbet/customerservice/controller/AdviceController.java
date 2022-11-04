package com.yazykov.sportbet.customerservice.controller;

import com.yazykov.sportbet.customerservice.exception.CreditCardNotFoundException;
import com.yazykov.sportbet.customerservice.exception.CustomerAlreadyExistException;
import com.yazykov.sportbet.customerservice.exception.CustomerNotFoundException;
import com.yazykov.sportbet.customerservice.exception.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Date;

@RestControllerAdvice
public class AdviceController {

    @ExceptionHandler(CreditCardNotFoundException.class)
    public ResponseEntity<?> handleCardNotFoundException(CreditCardNotFoundException ex) {
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setTitle("change card id");
        errorDetails.setDetail(ex.getMessage());
        errorDetails.setDevMessage(ex.getClass().getName());
        errorDetails.setTimeStamp(new Date().getTime());
        errorDetails.setStatus(HttpStatus.BAD_REQUEST.value());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
    }

    @ExceptionHandler(CustomerAlreadyExistException.class)
    public ResponseEntity<?> handleCustomerExistsException(CustomerAlreadyExistException ex) {
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setTitle("change email");
        errorDetails.setDetail(ex.getMessage());
        errorDetails.setDevMessage(ex.getClass().getName());
        errorDetails.setTimeStamp(new Date().getTime());
        errorDetails.setStatus(HttpStatus.BAD_REQUEST.value());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<?> handleCustomerNotFoundException(CustomerNotFoundException ex) {
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setTitle("change customer id");
        errorDetails.setDetail(ex.getMessage());
        errorDetails.setDevMessage(ex.getClass().getName());
        errorDetails.setTimeStamp(new Date().getTime());
        errorDetails.setStatus(HttpStatus.BAD_REQUEST.value());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
    }
}
