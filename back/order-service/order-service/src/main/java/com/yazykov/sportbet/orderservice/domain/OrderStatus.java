package com.yazykov.sportbet.orderservice.domain;

public enum OrderStatus {
    CREATED,
    ODD_APPROVED,
    CUSTOMER_APPROVED,
    PAYMENT_APPROVED,
    RESULT_SENDED,
    FINISHED,
    DECLINED
}
