package com.sleeved.looter.domain.repository.atlas;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sleeved.looter.domain.entity.atlas.TcgPlayerPrice;
import com.sleeved.looter.domain.entity.atlas.TcgPlayerReporting;

@Repository
public interface TcgPlayerPriceRepository extends JpaRepository<TcgPlayerPrice, Integer> {
  Optional<TcgPlayerPrice> findByTypeAndTcgPlayerReporting(String tcgPlayerPriceType, TcgPlayerReporting card);
}
