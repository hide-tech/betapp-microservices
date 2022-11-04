package com.yazykov.sportbet.resultservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDetails {

    private String title;
    private Integer status;
    private String detail;
    private Long timeStamp;
    private String devMessage;
}
