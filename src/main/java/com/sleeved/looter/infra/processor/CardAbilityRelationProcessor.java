package com.sleeved.looter.infra.processor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.sleeved.looter.domain.entity.atlas.Ability;
import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.entity.atlas.CardAbility;
import com.sleeved.looter.domain.service.AbilityService;
import com.sleeved.looter.infra.dto.AbilityDTO;
import com.sleeved.looter.infra.mapper.AbilityMapper;
import com.sleeved.looter.infra.mapper.CardAbilityMapper;

@Component
public class CardAbilityRelationProcessor implements CardRelationProcessor<AbilityDTO, CardAbility> {

  private final AbilityMapper abilityMapper;
  private final AbilityService abilityService;
  private final CardAbilityMapper cardAbilityMapper;

  public CardAbilityRelationProcessor(
      AbilityMapper abilityMapper,
      AbilityService abilityService,
      CardAbilityMapper cardAbilityMapper) {
    this.abilityMapper = abilityMapper;
    this.abilityService = abilityService;
    this.cardAbilityMapper = cardAbilityMapper;
  }

  @Override
  public List<CardAbility> process(List<AbilityDTO> abilitiesDTO, Card card) {
    List<CardAbility> cardAbilities = new ArrayList<>();
    if (abilitiesDTO == null || abilitiesDTO.isEmpty()) {
      return cardAbilities;
    }

    List<Ability> abilitiesToFind = abilityMapper.toListEntity(abilitiesDTO);

    for (Ability abilityToFind : abilitiesToFind) {
      Ability abilityFound = abilityService.getByNameAndTypeAndText(abilityToFind);
      CardAbility cardAbility = cardAbilityMapper.toEntity(abilityFound, card);
      if (cardAbility == null) {
        continue;
      }
      cardAbilities.add(cardAbility);
    }

    return cardAbilities;
  }
}
