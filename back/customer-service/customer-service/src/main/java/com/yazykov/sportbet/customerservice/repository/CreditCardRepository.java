package com.yazykov.sportbet.customerservice.repository;

import com.yazykov.sportbet.customerservice.domain.CreditCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditCardRepository extends JpaRepository<CreditCard, Long> {
}
