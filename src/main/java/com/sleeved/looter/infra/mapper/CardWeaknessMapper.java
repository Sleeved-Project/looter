package com.sleeved.looter.infra.mapper;

import org.springframework.stereotype.Component;

import com.sleeved.looter.domain.entity.atlas.Weakness;
import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.entity.atlas.CardWeakness;

@Component
public class CardWeaknessMapper {
  public CardWeakness toEntity(Weakness weakness, Card card) {
    if (weakness == null || card == null) {
      return null;
    }
    CardWeakness cardWeakness = new CardWeakness();
    cardWeakness.setWeakness(weakness);
    cardWeakness.setCard(card);
    return cardWeakness;
  }
}