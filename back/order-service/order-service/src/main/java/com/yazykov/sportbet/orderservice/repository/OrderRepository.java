package com.yazykov.sportbet.orderservice.repository;

import com.yazykov.sportbet.orderservice.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("select distinct o from Order o where o.customerId = ?1")
    List<Order> findAllByCustomerId(Long customerId);

    @Query("select o from Order o where o.orderId = ?1")
    Optional<Order> findByOrderId(String orderId);
}
