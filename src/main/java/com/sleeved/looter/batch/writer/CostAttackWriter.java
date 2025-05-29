package com.sleeved.looter.batch.writer;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.sleeved.looter.common.util.Constantes;
import com.sleeved.looter.domain.entity.atlas.CostAttack;
import com.sleeved.looter.domain.service.CostAttackService;
import com.sleeved.looter.infra.dto.CostAttackEntitiesProcessedDTO;
import com.sleeved.looter.infra.service.LooterScrapingErrorHandler;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CostAttackWriter implements ItemWriter<CostAttackEntitiesProcessedDTO> {

  private final CostAttackService costAttackService;

  private final LooterScrapingErrorHandler looterScrapingErrorHandler;

  public CostAttackWriter(
      LooterScrapingErrorHandler looterScrapingErrorHandler, CostAttackService costAttackService) {
    this.costAttackService = costAttackService;
    this.looterScrapingErrorHandler = looterScrapingErrorHandler;
  }

  @Override
  public void write(Chunk<? extends CostAttackEntitiesProcessedDTO> chunk) throws Exception {
    for (CostAttackEntitiesProcessedDTO costAttackEntities : chunk) {
      try {
        for (CostAttack costAttack : costAttackEntities.getCostAttacks()) {
          costAttackService.getOrCreate(costAttack);
        }
      } catch (Exception e) {
        String formatedItem = looterScrapingErrorHandler.formatErrorItem(
            Constantes.COST_ATTACK_CARD_ENTITIES_ITEM,
            costAttackEntities.toString());
        looterScrapingErrorHandler.handle(e, Constantes.SETS_WEAKNESS_RESISTANCE_ENTITIES_WRITER_CONTEXT,
            Constantes.WRITE_ACTION,
            formatedItem);
      }
    }
  }
}
