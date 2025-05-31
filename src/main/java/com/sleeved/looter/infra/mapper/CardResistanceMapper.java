package com.sleeved.looter.infra.mapper;

import org.springframework.stereotype.Component;

import com.sleeved.looter.domain.entity.atlas.Resistance;
import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.entity.atlas.CardResistance;

@Component
public class CardResistanceMapper {
  public CardResistance toEntity(Resistance resistance, Card card) {
    if (resistance == null || card == null) {
      return null;
    }
    CardResistance cardResistance = new CardResistance();
    cardResistance.setResistance(resistance);
    cardResistance.setCard(card);
    return cardResistance;
  }
}