package com.yazykov.sportbet.resultservice.dto.event;

import com.yazykov.sportbet.resultservice.domain.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderFinish {

    private String orderId;
    private Status status;
}
