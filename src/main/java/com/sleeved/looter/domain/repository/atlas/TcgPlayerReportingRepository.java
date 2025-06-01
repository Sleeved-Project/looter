package com.sleeved.looter.domain.repository.atlas;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.entity.atlas.TcgPlayerReporting;

@Repository
public interface TcgPlayerReportingRepository extends JpaRepository<TcgPlayerReporting, Integer> {
  Optional<TcgPlayerReporting> findByUpdatedAtAndCard(LocalDate priceUpdatedAt, Card card);
}
