package com.sleeved.looter.infra.mapper;

import org.springframework.stereotype.Component;

import com.sleeved.looter.domain.entity.atlas.Attack;
import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.entity.atlas.CardAttack;

@Component
public class CardAttackMapper {
  public CardAttack toEntity(Attack attack, Card card) {
    if (attack == null || card == null) {
      return null;
    }
    CardAttack cardAttack = new CardAttack();
    cardAttack.setAttack(attack);
    cardAttack.setCard(card);
    return cardAttack;
  }
}