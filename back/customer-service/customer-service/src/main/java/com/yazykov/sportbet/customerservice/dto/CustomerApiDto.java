package com.yazykov.sportbet.customerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerApiDto {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private String photoUrl;
    private List<CreditCardApiDto> cards;
}
