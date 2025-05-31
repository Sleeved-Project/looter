package com.sleeved.looter.infra.processor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.entity.atlas.CardResistance;
import com.sleeved.looter.domain.entity.atlas.Resistance;
import com.sleeved.looter.domain.service.ResistanceService;
import com.sleeved.looter.infra.dto.ResistanceDTO;
import com.sleeved.looter.infra.mapper.CardResistanceMapper;

@Component
public class CardResistanceRelationProcessor implements CardRelationProcessor<ResistanceDTO, CardResistance> {

  private final ResistanceProcessor resistanceProcessor;
  private final ResistanceService resistanceService;
  private final CardResistanceMapper cardResistanceMapper;

  public CardResistanceRelationProcessor(
      ResistanceProcessor resistanceProcessor,
      ResistanceService resistanceService,
      CardResistanceMapper cardResistanceMapper) {
    this.resistanceProcessor = resistanceProcessor;
    this.resistanceService = resistanceService;
    this.cardResistanceMapper = cardResistanceMapper;
  }

  @Override
  public List<CardResistance> process(List<ResistanceDTO> resistancesDTO, Card card) {
    List<CardResistance> cardResistances = new ArrayList<>();
    if (resistancesDTO == null || resistancesDTO.isEmpty()) {
      return cardResistances;
    }

    List<Resistance> resistancesToFind = resistanceProcessor.processFromDTOs(resistancesDTO);

    for (Resistance resistanceToFind : resistancesToFind) {
      Resistance resistanceFound = resistanceService.getByTypeAndValue(resistanceToFind);
      CardResistance cardResistance = cardResistanceMapper.toEntity(resistanceFound, card);
      if (cardResistance == null) {
        continue;
      }
      cardResistances.add(cardResistance);
    }

    return cardResistances;
  }
}