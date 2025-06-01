package com.sleeved.looter.domain.service;

import org.springframework.stereotype.Service;

import com.sleeved.looter.domain.entity.atlas.CardMarketPrice;
import com.sleeved.looter.domain.repository.atlas.CardMarketPriceRepository;

@Service
public class CardMarketPriceService {

  private final CardMarketPriceRepository cardMarketPriceRepository;

  public CardMarketPriceService(CardMarketPriceRepository cardMarketPriceRepository) {
    this.cardMarketPriceRepository = cardMarketPriceRepository;
  }

  public CardMarketPrice getOrCreate(CardMarketPrice cardMarketPrice) {
    return cardMarketPriceRepository.findByUpdatedAtAndCard(
        cardMarketPrice.getUpdatedAt(), cardMarketPrice.getCard())
        .orElseGet(() -> cardMarketPriceRepository.save(cardMarketPrice));
  }
}