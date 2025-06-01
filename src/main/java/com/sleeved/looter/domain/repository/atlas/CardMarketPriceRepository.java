package com.sleeved.looter.domain.repository.atlas;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.entity.atlas.CardMarketPrice;

@Repository
public interface CardMarketPriceRepository extends JpaRepository<CardMarketPrice, Integer> {
  Optional<CardMarketPrice> findByUpdatedAtAndCard(LocalDate priceUpdatedAt, Card card);
}
