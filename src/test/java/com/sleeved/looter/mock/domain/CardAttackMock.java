package com.sleeved.looter.mock.domain;

import com.sleeved.looter.domain.entity.atlas.Attack;
import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.entity.atlas.CardAttack;

public class CardAttackMock {
  public static CardAttack createMockCardAttack(Attack attack, Card card) {
    CardAttack cardAttack = new CardAttack();
    cardAttack.setAttack(attack);
    cardAttack.setCard(card);
    return cardAttack;
  }

  public static CardAttack createMockCardAttackSavedInDb(int id, Attack attack, Card card) {
    CardAttack cardAttack = createMockCardAttack(attack, card);
    cardAttack.setId(id);
    return cardAttack;
  }
}