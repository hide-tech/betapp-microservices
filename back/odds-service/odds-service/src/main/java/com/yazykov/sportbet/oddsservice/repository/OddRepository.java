package com.yazykov.sportbet.oddsservice.repository;

import com.yazykov.sportbet.oddsservice.domain.Odd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OddRepository extends JpaRepository<Odd, Long> {

    @Query("select distinct o from Odd o where o.eventId = ?1")
    Odd findByEventId(String eventId);
}
