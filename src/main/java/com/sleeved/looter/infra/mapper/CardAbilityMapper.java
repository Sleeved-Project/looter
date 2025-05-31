package com.sleeved.looter.infra.mapper;

import org.springframework.stereotype.Component;

import com.sleeved.looter.domain.entity.atlas.Ability;
import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.entity.atlas.CardAbility;

@Component
public class CardAbilityMapper {
  public CardAbility toEntity(Ability abilty, Card card) {
    if (abilty == null || card == null) {
      return null;
    }
    CardAbility cardAbility = new CardAbility();
    cardAbility.setAbility(abilty);
    cardAbility.setCard(card);
    return cardAbility;
  }
}
