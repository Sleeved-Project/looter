package com.sleeved.looter.mock.domain;

import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.entity.atlas.CardResistance;
import com.sleeved.looter.domain.entity.atlas.Resistance;

public class CardResistanceMock {
  public static CardResistance createMockCardResistance(Resistance resistance, Card card) {
    CardResistance cardResistance = new CardResistance();
    cardResistance.setResistance(resistance);
    cardResistance.setCard(card);
    return cardResistance;
  }

  public static CardResistance createMockCardResistanceSavedInDb(int id, Resistance resistance, Card card) {
    CardResistance cardResistance = createMockCardResistance(resistance, card);
    cardResistance.setId(id);
    return cardResistance;
  }
}