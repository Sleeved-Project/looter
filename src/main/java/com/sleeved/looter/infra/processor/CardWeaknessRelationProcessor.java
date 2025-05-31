package com.sleeved.looter.infra.processor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.entity.atlas.CardWeakness;
import com.sleeved.looter.domain.entity.atlas.Weakness;
import com.sleeved.looter.domain.service.WeaknessService;
import com.sleeved.looter.infra.dto.WeaknessDTO;
import com.sleeved.looter.infra.mapper.CardWeaknessMapper;

@Component
public class CardWeaknessRelationProcessor implements CardRelationProcessor<WeaknessDTO, CardWeakness> {

  private final WeaknessProcessor weaknessProcessor;
  private final WeaknessService weaknessService;
  private final CardWeaknessMapper cardWeaknessMapper;

  public CardWeaknessRelationProcessor(
      WeaknessProcessor weaknessProcessor,
      WeaknessService weaknessService,
      CardWeaknessMapper cardWeaknessMapper) {
    this.weaknessProcessor = weaknessProcessor;
    this.weaknessService = weaknessService;
    this.cardWeaknessMapper = cardWeaknessMapper;
  }

  @Override
  public List<CardWeakness> process(List<WeaknessDTO> weaknessesDTO, Card card) {
    List<CardWeakness> cardWeaknesses = new ArrayList<>();
    if (weaknessesDTO == null || weaknessesDTO.isEmpty()) {
      return cardWeaknesses;
    }

    List<Weakness> weaknessesToFind = weaknessProcessor.processFromDTOs(weaknessesDTO);

    for (Weakness weaknessToFind : weaknessesToFind) {
      Weakness weaknessFound = weaknessService.getByTypeAndValue(weaknessToFind);
      CardWeakness cardWeakness = cardWeaknessMapper.toEntity(weaknessFound, card);
      if (cardWeakness == null) {
        continue;
      }
      cardWeaknesses.add(cardWeakness);
    }

    return cardWeaknesses;
  }
}