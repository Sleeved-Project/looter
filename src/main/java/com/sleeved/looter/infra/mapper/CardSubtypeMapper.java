package com.sleeved.looter.infra.mapper;

import org.springframework.stereotype.Component;

import com.sleeved.looter.domain.entity.atlas.Subtype;
import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.entity.atlas.CardSubtype;

@Component
public class CardSubtypeMapper {
  public CardSubtype toEntity(Subtype subtype, Card card) {
    if (subtype == null || card == null) {
      return null;
    }
    CardSubtype cardSubtype = new CardSubtype();
    cardSubtype.setSubtype(subtype);
    cardSubtype.setCard(card);
    return cardSubtype;
  }
}