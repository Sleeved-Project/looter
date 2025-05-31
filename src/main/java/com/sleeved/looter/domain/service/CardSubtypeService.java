package com.sleeved.looter.domain.service;

import org.springframework.stereotype.Service;

import com.sleeved.looter.domain.entity.atlas.CardSubtype;
import com.sleeved.looter.domain.repository.atlas.CardSubtypeRepository;

@Service
public class CardSubtypeService {

  private final CardSubtypeRepository cardSubtypeRepository;

  public CardSubtypeService(CardSubtypeRepository cardSubtypeRepository) {
    this.cardSubtypeRepository = cardSubtypeRepository;
  }

  public CardSubtype getOrCreate(CardSubtype cardSubtype) {
    return cardSubtypeRepository.findBySubtypeAndCard(cardSubtype.getSubtype(), cardSubtype.getCard())
        .orElseGet(() -> cardSubtypeRepository.save(cardSubtype));
  }
}