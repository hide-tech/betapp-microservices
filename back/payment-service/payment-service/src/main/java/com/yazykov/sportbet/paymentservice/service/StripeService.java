package com.yazykov.sportbet.paymentservice.service;

import com.yazykov.sportbet.paymentservice.domain.CreditCard;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class StripeService {

    public boolean pay(CreditCard creditCard, BigDecimal amount){
        double res = Math.random() * 10;
        if (res>5){
            return true;
        }
        return false;
    }
}
