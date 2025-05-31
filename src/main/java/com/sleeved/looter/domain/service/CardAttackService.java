package com.sleeved.looter.domain.service;

import org.springframework.stereotype.Service;

import com.sleeved.looter.domain.entity.atlas.CardAttack;
import com.sleeved.looter.domain.repository.atlas.CardAttackRepository;

@Service
public class CardAttackService {

  private final CardAttackRepository cardAttackRepository;

  public CardAttackService(CardAttackRepository cardAttackRepository) {
    this.cardAttackRepository = cardAttackRepository;
  }

  public CardAttack getOrCreate(CardAttack cardAttack) {
    return cardAttackRepository.findByAttackAndCard(cardAttack.getAttack(), cardAttack.getCard())
        .orElseGet(() -> cardAttackRepository.save(cardAttack));
  }
}