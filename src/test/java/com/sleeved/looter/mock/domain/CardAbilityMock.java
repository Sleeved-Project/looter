package com.sleeved.looter.mock.domain;

import com.sleeved.looter.domain.entity.atlas.Ability;
import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.entity.atlas.CardAbility;

public class CardAbilityMock {
  public static CardAbility createMockCardAbility(Ability ability, Card card) {
    CardAbility cardAbility = new CardAbility();
    cardAbility.setAbility(ability);
    cardAbility.setCard(card);
    return cardAbility;
  }

  public static CardAbility createMockCardAbilitySavedInDb(int id, Ability ability, Card card) {
    CardAbility cardAbility = createMockCardAbility(ability, card);
    cardAbility.setId(id);
    return cardAbility;
  }
}