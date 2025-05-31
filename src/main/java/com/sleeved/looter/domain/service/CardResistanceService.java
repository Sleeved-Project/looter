package com.sleeved.looter.domain.service;

import org.springframework.stereotype.Service;

import com.sleeved.looter.domain.entity.atlas.CardResistance;
import com.sleeved.looter.domain.repository.atlas.CardResistanceRepository;

@Service
public class CardResistanceService {

  private final CardResistanceRepository cardResistanceRepository;

  public CardResistanceService(CardResistanceRepository cardResistanceRepository) {
    this.cardResistanceRepository = cardResistanceRepository;
  }

  public CardResistance getOrCreate(CardResistance cardResistance) {
    return cardResistanceRepository.findByResistanceAndCard(
        cardResistance.getResistance(), cardResistance.getCard())
        .orElseGet(() -> cardResistanceRepository.save(cardResistance));
  }
}