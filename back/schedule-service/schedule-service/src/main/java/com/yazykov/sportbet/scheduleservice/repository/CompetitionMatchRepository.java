package com.yazykov.sportbet.scheduleservice.repository;

import com.yazykov.sportbet.scheduleservice.domain.CompetitionMatch;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompetitionMatchRepository extends PagingAndSortingRepository<CompetitionMatch, Long> {

}
