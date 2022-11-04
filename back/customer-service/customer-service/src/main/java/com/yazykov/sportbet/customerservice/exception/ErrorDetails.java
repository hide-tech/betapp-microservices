package com.yazykov.sportbet.customerservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDetails {
    private String title;
    private Integer status;
    private String detail;
    private Long timeStamp;
    private String devMessage;
}
