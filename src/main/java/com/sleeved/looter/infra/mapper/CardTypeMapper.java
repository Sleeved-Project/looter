package com.sleeved.looter.infra.mapper;

import org.springframework.stereotype.Component;

import com.sleeved.looter.domain.entity.atlas.Type;
import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.entity.atlas.CardType;

@Component
public class CardTypeMapper {
  public CardType toEntity(Type type, Card card) {
    if (type == null || card == null) {
      return null;
    }
    CardType cardType = new CardType();
    cardType.setType(type);
    cardType.setCard(card);
    return cardType;
  }
}