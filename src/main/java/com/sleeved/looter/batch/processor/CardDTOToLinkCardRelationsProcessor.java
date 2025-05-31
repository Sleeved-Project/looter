package com.sleeved.looter.batch.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.sleeved.looter.common.util.Constantes;
import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.service.CardService;
import com.sleeved.looter.infra.dto.CardDTO;
import com.sleeved.looter.infra.dto.LinkCardRelationsEntitiesProcessedDTO;
import com.sleeved.looter.infra.processor.CardAbilityRelationProcessor;
import com.sleeved.looter.infra.processor.CardAttackRelationProcessor;
import com.sleeved.looter.infra.processor.CardResistanceRelationProcessor;
import com.sleeved.looter.infra.processor.CardSubtypeRelationProcessor;
import com.sleeved.looter.infra.processor.CardTypeRelationProcessor;
import com.sleeved.looter.infra.processor.CardWeaknessRelationProcessor;
import com.sleeved.looter.infra.service.LooterScrapingErrorHandler;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CardDTOToLinkCardRelationsProcessor
    implements ItemProcessor<CardDTO, LinkCardRelationsEntitiesProcessedDTO> {

  private final CardService cardService;
  private final LooterScrapingErrorHandler looterScrapingErrorHandler;
  private final CardAbilityRelationProcessor cardAbilityRelationProcessor;
  private final CardAttackRelationProcessor cardAttackRelationProcessor;
  private final CardResistanceRelationProcessor cardResistanceRelationProcessor;
  private final CardSubtypeRelationProcessor cardSubtypeRelationProcessor;
  private final CardTypeRelationProcessor cardTypeRelationProcessor;
  private final CardWeaknessRelationProcessor cardWeaknessRelationProcessor;

  public CardDTOToLinkCardRelationsProcessor(
      CardService cardService,
      LooterScrapingErrorHandler looterScrapingErrorHandler,
      CardAbilityRelationProcessor cardAbilityRelationProcessor,
      CardAttackRelationProcessor cardAttackRelationProcessor,
      CardResistanceRelationProcessor cardResistanceRelationProcessor,
      CardSubtypeRelationProcessor cardSubtypeRelationProcessor,
      CardTypeRelationProcessor cardTypeRelationProcessor,
      CardWeaknessRelationProcessor cardWeaknessRelationProcessor) {
    this.cardService = cardService;
    this.looterScrapingErrorHandler = looterScrapingErrorHandler;
    this.cardAbilityRelationProcessor = cardAbilityRelationProcessor;
    this.cardAttackRelationProcessor = cardAttackRelationProcessor;
    this.cardResistanceRelationProcessor = cardResistanceRelationProcessor;
    this.cardSubtypeRelationProcessor = cardSubtypeRelationProcessor;
    this.cardTypeRelationProcessor = cardTypeRelationProcessor;
    this.cardWeaknessRelationProcessor = cardWeaknessRelationProcessor;
  }

  @Override
  public LinkCardRelationsEntitiesProcessedDTO process(CardDTO item) {
    try {
      Card card = cardService.getById(item.getId());

      LinkCardRelationsEntitiesProcessedDTO dto = new LinkCardRelationsEntitiesProcessedDTO();

      dto.setCardAbilities(cardAbilityRelationProcessor.process(item.getAbilities(), card));
      dto.setCardAttacks(cardAttackRelationProcessor.process(item.getAttacks(), card));
      dto.setCardResistances(cardResistanceRelationProcessor.process(item.getResistances(), card));
      dto.setCardSubtypes(cardSubtypeRelationProcessor.process(item.getSubtypes(), card));
      dto.setCardTypes(cardTypeRelationProcessor.process(item.getTypes(), card));
      dto.setCardWeaknesses(cardWeaknessRelationProcessor.process(item.getWeaknesses(), card));

      return dto;
    } catch (Exception e) {
      String formatedItem = looterScrapingErrorHandler.formatErrorItem(
          Constantes.CARD_DTO_ITEM,
          item.toString());
      looterScrapingErrorHandler.handle(e,
          Constantes.CARD_DTO_TO_LINK_CARD_RELATIONS_ENTITIES_PROCESSOR_CONTEXT,
          Constantes.PROCESSOR_ACTION,
          formatedItem);
      return null;
    }
  }
}