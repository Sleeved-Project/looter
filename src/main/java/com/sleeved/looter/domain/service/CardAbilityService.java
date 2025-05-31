package com.sleeved.looter.domain.service;

import org.springframework.stereotype.Service;

import com.sleeved.looter.domain.entity.atlas.CardAbility;
import com.sleeved.looter.domain.repository.atlas.CardAbilityRepository;

@Service
public class CardAbilityService {

  private final CardAbilityRepository cardAbilityRepository;

  public CardAbilityService(CardAbilityRepository cardAbilityRepository) {
    this.cardAbilityRepository = cardAbilityRepository;
  }

  public CardAbility getOrCreate(CardAbility cardAbility) {
    return cardAbilityRepository.findByAbilityAndCard(cardAbility.getAbility(), cardAbility.getCard())
        .orElseGet(() -> cardAbilityRepository.save(cardAbility));
  }
}