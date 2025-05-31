package com.sleeved.looter.mock.domain;

import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.entity.atlas.CardSubtype;
import com.sleeved.looter.domain.entity.atlas.Subtype;

public class CardSubtypeMock {
  public static CardSubtype createMockCardSubtype(Subtype subtype, Card card) {
    CardSubtype cardSubtype = new CardSubtype();
    cardSubtype.setSubtype(subtype);
    cardSubtype.setCard(card);
    return cardSubtype;
  }

  public static CardSubtype createMockCardSubtypeSavedInDb(int id, Subtype subtype, Card card) {
    CardSubtype cardSubtype = createMockCardSubtype(subtype, card);
    cardSubtype.setId(id);
    return cardSubtype;
  }
}