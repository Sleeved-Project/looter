package com.sleeved.looter.domain.service;

import org.springframework.stereotype.Service;

import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.repository.atlas.CardRepository;

@Service
public class CardService {

  private final CardRepository cardRepository;

  public CardService(CardRepository cardRepository) {
    this.cardRepository = cardRepository;
  }

  public Card getOrCreate(Card card) {
    return cardRepository.findById(card.getId())
        .orElseGet(() -> cardRepository.save(card));
  }
}
