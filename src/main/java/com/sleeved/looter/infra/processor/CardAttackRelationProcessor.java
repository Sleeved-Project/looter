package com.sleeved.looter.infra.processor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.sleeved.looter.domain.entity.atlas.Attack;
import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.entity.atlas.CardAttack;
import com.sleeved.looter.domain.service.AttackService;
import com.sleeved.looter.infra.dto.AttackDTO;
import com.sleeved.looter.infra.mapper.AttackMapper;
import com.sleeved.looter.infra.mapper.CardAttackMapper;

@Component
public class CardAttackRelationProcessor implements CardRelationProcessor<AttackDTO, CardAttack> {

  private final AttackMapper attackMapper;
  private final AttackService attackService;
  private final CardAttackMapper cardAttackMapper;

  public CardAttackRelationProcessor(
      AttackMapper attackMapper,
      AttackService attackService,
      CardAttackMapper cardAttackMapper) {
    this.attackMapper = attackMapper;
    this.attackService = attackService;
    this.cardAttackMapper = cardAttackMapper;
  }

  @Override
  public List<CardAttack> process(List<AttackDTO> attacksDTO, Card card) {
    List<CardAttack> cardAttacks = new ArrayList<>();
    if (attacksDTO == null || attacksDTO.isEmpty()) {
      return cardAttacks;
    }

    List<Attack> attacksToFind = attackMapper.toListEntity(attacksDTO);

    for (Attack attackToFind : attacksToFind) {
      Attack attackFound = attackService.getByNameAndDamageAndConvertedEnegyCostAndText(attackToFind);
      CardAttack cardAttack = cardAttackMapper.toEntity(attackFound, card);
      if (cardAttack == null) {
        continue;
      }
      cardAttacks.add(cardAttack);
    }

    return cardAttacks;
  }
}
