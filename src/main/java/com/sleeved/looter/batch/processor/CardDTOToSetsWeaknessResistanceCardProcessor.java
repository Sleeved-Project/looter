package com.sleeved.looter.batch.processor;

import java.util.List;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.sleeved.looter.common.util.Constantes;
import com.sleeved.looter.domain.entity.atlas.Legalities;
import com.sleeved.looter.domain.entity.atlas.Resistance;
import com.sleeved.looter.domain.entity.atlas.Set;
import com.sleeved.looter.domain.entity.atlas.Weakness;
import com.sleeved.looter.domain.service.LegalitiesService;
import com.sleeved.looter.domain.service.TypeService;
import com.sleeved.looter.infra.dto.CardDTO;
import com.sleeved.looter.infra.dto.SetsWeaknessResistanceCardEntitiesProcessedDTO;
import com.sleeved.looter.infra.mapper.LegalitiesMapper;
import com.sleeved.looter.infra.mapper.SetMapper;
import com.sleeved.looter.infra.mapper.TypeMapper;
import com.sleeved.looter.infra.processor.ResistanceProcessor;
import com.sleeved.looter.infra.processor.WeaknessProcessor;
import com.sleeved.looter.infra.service.LooterScrapingErrorHandler;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CardDTOToSetsWeaknessResistanceCardProcessor
    implements ItemProcessor<CardDTO, SetsWeaknessResistanceCardEntitiesProcessedDTO> {

  private final LooterScrapingErrorHandler looterScrapingErrorHandler;
  private final SetMapper setMapper;
  private final LegalitiesMapper legalitiesMapper;
  private final LegalitiesService legalitiesService;
  private final WeaknessProcessor weaknessProcessor;
  private final ResistanceProcessor resistanceProcessor;

  public CardDTOToSetsWeaknessResistanceCardProcessor(
      LegalitiesMapper legalitiesMapper, LegalitiesService legalitiesService, SetMapper setMapper,
      TypeMapper typeMapper,
      TypeService typeService,
      WeaknessProcessor weaknessProcessor,
      ResistanceProcessor resistanceProcessor,
      LooterScrapingErrorHandler looterScrapingErrorHandler) {
    this.legalitiesMapper = legalitiesMapper;
    this.legalitiesService = legalitiesService;
    this.setMapper = setMapper;
    this.weaknessProcessor = weaknessProcessor;
    this.resistanceProcessor = resistanceProcessor;
    this.looterScrapingErrorHandler = looterScrapingErrorHandler;
  }

  @Override
  public SetsWeaknessResistanceCardEntitiesProcessedDTO process(CardDTO item) {
    try {
      SetsWeaknessResistanceCardEntitiesProcessedDTO setsWeaknessResistanceCardEntitiesProcessedDTO = new SetsWeaknessResistanceCardEntitiesProcessedDTO();
      Legalities setLegalitiesToFind = legalitiesMapper.toEntity(item.getSet().getLegalities());
      Legalities setLegalitiesFound = legalitiesService.getByStandardExpandedUnlimited(
          setLegalitiesToFind);

      Set set = setMapper.toEntity(item.getSet(), setLegalitiesFound);

      List<Weakness> weaknesses = weaknessProcessor.processFromDTOs(item.getWeaknesses());
      List<Resistance> resistances = resistanceProcessor.processFromDTOs(item.getResistances());

      setsWeaknessResistanceCardEntitiesProcessedDTO.setSet(set);
      setsWeaknessResistanceCardEntitiesProcessedDTO.setWeaknesses(weaknesses);
      setsWeaknessResistanceCardEntitiesProcessedDTO.setResistances(resistances);
      return setsWeaknessResistanceCardEntitiesProcessedDTO;
    } catch (Exception e) {
      String formatedItem = looterScrapingErrorHandler.formatErrorItem(
          Constantes.CARD_DTO_ITEM,
          item.toString());
      looterScrapingErrorHandler.handle(e,
          Constantes.CARD_DTO_TO_SETS_WEAKNESS_RESISTANCE_CARD_ENTITIES_PROCESSOR_CONTEXT,
          Constantes.PROCESSOR_ACTION,
          formatedItem);
      return null;
    }
  }
}
