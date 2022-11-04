package com.yazykov.sportbet.resultservice.repository;

import com.yazykov.sportbet.resultservice.domain.ResultInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ResultInvoiceRepository extends JpaRepository<ResultInvoice, Long> {

    @Query("select distinct r from ResultInvoice r where r.orderId = ?1")
    Optional<ResultInvoice> findByOrderId(String orderId);

    @Query("select distinct r from ResultInvoice r where r.status = PENDING and r.checkTime before ?1")
    List<ResultInvoice> findAllPending(LocalDateTime check);

    @Query("select distinct r from ResultInvoice r where r.status = WIN_PAY_PENDING and r.checkTime before ?1")
    List<ResultInvoice> findAllWonPending(LocalDateTime check);

    @Query("select distinct r from ResultInvoice r where r.status = WIN_PAY_ERROR and r.checkTime before ?1")
    List<ResultInvoice> findAllWonErrorPending(LocalDateTime check);
}
