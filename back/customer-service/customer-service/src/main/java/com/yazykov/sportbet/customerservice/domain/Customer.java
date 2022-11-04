package com.yazykov.sportbet.customerservice.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String surname;
    private String email;
    private String photoUrl;
    private String passportSheet1PhotoUrl;
    private String passportSheet2PhotoUrl;
    @Enumerated(EnumType.STRING)
    private Status status;
    @OneToMany(mappedBy = "customer")
    private List<CreditCard> cards;
}
