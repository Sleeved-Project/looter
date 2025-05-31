package com.sleeved.looter.mock.domain;

import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.entity.atlas.CardWeakness;
import com.sleeved.looter.domain.entity.atlas.Weakness;

public class CardWeaknessMock {
  public static CardWeakness createMockCardWeakness(Weakness weakness, Card card) {
    CardWeakness cardWeakness = new CardWeakness();
    cardWeakness.setWeakness(weakness);
    cardWeakness.setCard(card);
    return cardWeakness;
  }

  public static CardWeakness createMockCardWeaknessSavedInDb(int id, Weakness weakness, Card card) {
    CardWeakness cardWeakness = createMockCardWeakness(weakness, card);
    cardWeakness.setId(id);
    return cardWeakness;
  }
}