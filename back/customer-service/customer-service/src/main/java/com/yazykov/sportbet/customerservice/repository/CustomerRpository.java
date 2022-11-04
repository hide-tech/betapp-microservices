package com.yazykov.sportbet.customerservice.repository;

import com.yazykov.sportbet.customerservice.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRpository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmail(String email);
}
