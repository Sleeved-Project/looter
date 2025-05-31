package com.sleeved.looter.mock.domain;

import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.entity.atlas.CardType;
import com.sleeved.looter.domain.entity.atlas.Type;

public class CardTypeMock {
  public static CardType createMockCardType(Type type, Card card) {
    CardType cardType = new CardType();
    cardType.setType(type);
    cardType.setCard(card);
    return cardType;
  }

  public static CardType createMockCardTypeSavedInDb(int id, Type type, Card card) {
    CardType cardType = createMockCardType(type, card);
    cardType.setId(id);
    return cardType;
  }
}