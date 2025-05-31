package com.sleeved.looter.domain.service;

import org.springframework.stereotype.Service;

import com.sleeved.looter.domain.entity.atlas.CardType;
import com.sleeved.looter.domain.repository.atlas.CardTypeRepository;

@Service
public class CardTypeService {

  private final CardTypeRepository cardTypeRepository;

  public CardTypeService(CardTypeRepository cardTypeRepository) {
    this.cardTypeRepository = cardTypeRepository;
  }

  public CardType getOrCreate(CardType cardType) {
    return cardTypeRepository.findByTypeAndCard(cardType.getType(), cardType.getCard())
        .orElseGet(() -> cardTypeRepository.save(cardType));
  }
}