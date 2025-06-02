package com.sleeved.looter.domain.service;

import org.springframework.stereotype.Service;

import com.sleeved.looter.domain.entity.atlas.TcgPlayerPrice;
import com.sleeved.looter.domain.repository.atlas.TcgPlayerPriceRepository;

@Service
public class TcgPlayerPriceService {

  private final TcgPlayerPriceRepository tcgPlayerPriceRepository;

  public TcgPlayerPriceService(TcgPlayerPriceRepository tcgPlayerPriceRepository) {
    this.tcgPlayerPriceRepository = tcgPlayerPriceRepository;
  }

  public TcgPlayerPrice getOrCreate(TcgPlayerPrice tcgPlayerPrice) {
    return tcgPlayerPriceRepository.findByTypeAndTcgPlayerReporting(
        tcgPlayerPrice.getType(), tcgPlayerPrice.getTcgPlayerReporting())
        .orElseGet(() -> tcgPlayerPriceRepository.save(tcgPlayerPrice));
  }
}