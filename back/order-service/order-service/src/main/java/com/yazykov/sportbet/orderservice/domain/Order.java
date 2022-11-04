package com.yazykov.sportbet.orderservice.domain;

import com.yazykov.sportbet.orderservice.dto.OddDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
@DynamicUpdate
@DynamicInsert
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    @Embedded
    private OrderDetail orderDetail;

    public static Order setOddToOrderDetail(OddDto odd, Order order) {
        OrderDetail od = order.getOrderDetail();
        od.setEventId(odd.getEventId());
        od.setEventDateTime(odd.getThresholdTime());
        od.setOdd(odd.getRatio());
        order.setOrderDetail(od);
        return order;
    }
}
