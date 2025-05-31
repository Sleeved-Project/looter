package com.sleeved.looter.domain.service;

import org.springframework.stereotype.Service;

import com.sleeved.looter.domain.entity.atlas.CardWeakness;
import com.sleeved.looter.domain.repository.atlas.CardWeaknessRepository;

@Service
public class CardWeaknessService {

  private final CardWeaknessRepository cardWeaknessRepository;

  public CardWeaknessService(CardWeaknessRepository cardWeaknessRepository) {
    this.cardWeaknessRepository = cardWeaknessRepository;
  }

  public CardWeakness getOrCreate(CardWeakness cardWeakness) {
    return cardWeaknessRepository.findByWeaknessAndCard(
        cardWeakness.getWeakness(), cardWeakness.getCard())
        .orElseGet(() -> cardWeaknessRepository.save(cardWeakness));
  }
}