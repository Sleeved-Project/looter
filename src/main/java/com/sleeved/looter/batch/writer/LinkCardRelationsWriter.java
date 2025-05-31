package com.sleeved.looter.batch.writer;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.sleeved.looter.common.util.Constantes;
import com.sleeved.looter.domain.entity.atlas.CardAbility;
import com.sleeved.looter.domain.entity.atlas.CardAttack;
import com.sleeved.looter.domain.entity.atlas.CardResistance;
import com.sleeved.looter.domain.entity.atlas.CardSubtype;
import com.sleeved.looter.domain.entity.atlas.CardType;
import com.sleeved.looter.domain.entity.atlas.CardWeakness;
import com.sleeved.looter.domain.service.CardAbilityService;
import com.sleeved.looter.domain.service.CardAttackService;
import com.sleeved.looter.domain.service.CardResistanceService;
import com.sleeved.looter.domain.service.CardSubtypeService;
import com.sleeved.looter.domain.service.CardTypeService;
import com.sleeved.looter.domain.service.CardWeaknessService;
import com.sleeved.looter.infra.dto.LinkCardRelationsEntitiesProcessedDTO;
import com.sleeved.looter.infra.service.LooterScrapingErrorHandler;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LinkCardRelationsWriter implements ItemWriter<LinkCardRelationsEntitiesProcessedDTO> {

  private final CardAbilityService cardAbilityService;
  private final CardAttackService cardAttackService;
  private final CardResistanceService cardResistanceService;
  private final CardSubtypeService cardSubtypeService;
  private final CardTypeService cardTypeService;
  private final CardWeaknessService cardWeaknessService;
  private final LooterScrapingErrorHandler looterScrapingErrorHandler;

  public LinkCardRelationsWriter(
      LooterScrapingErrorHandler looterScrapingErrorHandler,
      CardAbilityService cardAbilityService,
      CardAttackService cardAttackService,
      CardResistanceService cardResistanceService,
      CardSubtypeService cardSubtypeService,
      CardTypeService cardTypeService,
      CardWeaknessService cardWeaknessService) {

    this.looterScrapingErrorHandler = looterScrapingErrorHandler;
    this.cardAbilityService = cardAbilityService;
    this.cardAttackService = cardAttackService;
    this.cardResistanceService = cardResistanceService;
    this.cardSubtypeService = cardSubtypeService;
    this.cardTypeService = cardTypeService;
    this.cardWeaknessService = cardWeaknessService;
  }

  @Override
  public void write(Chunk<? extends LinkCardRelationsEntitiesProcessedDTO> chunk) throws Exception {
    for (LinkCardRelationsEntitiesProcessedDTO linkCardRelationsEntities : chunk) {
      try {
        for (CardAbility cardAbility : linkCardRelationsEntities.getCardAbilities()) {
          cardAbilityService.getOrCreate(cardAbility);
        }
        for (CardAttack cardAttack : linkCardRelationsEntities.getCardAttacks()) {
          cardAttackService.getOrCreate(cardAttack);
        }
        for (CardResistance cardResistance : linkCardRelationsEntities.getCardResistances()) {
          cardResistanceService.getOrCreate(cardResistance);
        }
        for (CardSubtype cardSubtype : linkCardRelationsEntities.getCardSubtypes()) {
          cardSubtypeService.getOrCreate(cardSubtype);
        }
        for (CardType cardType : linkCardRelationsEntities.getCardTypes()) {
          cardTypeService.getOrCreate(cardType);
        }
        for (CardWeakness cardWeakness : linkCardRelationsEntities.getCardWeaknesses()) {
          cardWeaknessService.getOrCreate(cardWeakness);
        }
      } catch (Exception e) {
        String formatedItem = looterScrapingErrorHandler.formatErrorItem(
            Constantes.LINK_CARD_RELATIONS_ENTITIES_ITEM,
            linkCardRelationsEntities.toString());
        looterScrapingErrorHandler.handle(e, Constantes.LINK_CARD_RELATIONS_ENTITIES_WRITER_CONTEXT,
            Constantes.WRITE_ACTION,
            formatedItem);
      }
    }
  }
}
