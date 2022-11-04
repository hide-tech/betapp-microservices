package com.yazykov.sportbet.customerservice.exception;

public class CustomerAlreadyExistException extends Exception{

    public CustomerAlreadyExistException(String message) {
        super(message);
    }
}
