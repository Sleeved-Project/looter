package com.sleeved.looter.batch.writer;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.sleeved.looter.common.util.Constantes;
import com.sleeved.looter.domain.entity.atlas.Resistance;
import com.sleeved.looter.domain.entity.atlas.Weakness;
import com.sleeved.looter.domain.service.ResistanceService;
import com.sleeved.looter.domain.service.SetService;
import com.sleeved.looter.domain.service.WeaknessService;
import com.sleeved.looter.infra.dto.SetsWeaknessResistanceCardEntitiesProcessedDTO;
import com.sleeved.looter.infra.service.LooterScrapingErrorHandler;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SetsWeaknessResistanceWriter implements ItemWriter<SetsWeaknessResistanceCardEntitiesProcessedDTO> {

  private final SetService setService;
  private final WeaknessService weaknessService;
  private final ResistanceService resistanceService;
  private final LooterScrapingErrorHandler looterScrapingErrorHandler;

  public SetsWeaknessResistanceWriter(SetService setService, WeaknessService weaknessService,
      ResistanceService resistanceService,
      LooterScrapingErrorHandler looterScrapingErrorHandler) {
    this.setService = setService;
    this.weaknessService = weaknessService;
    this.resistanceService = resistanceService;
    this.looterScrapingErrorHandler = looterScrapingErrorHandler;
  }

  @Override
  public void write(Chunk<? extends SetsWeaknessResistanceCardEntitiesProcessedDTO> chunk) throws Exception {
    for (SetsWeaknessResistanceCardEntitiesProcessedDTO setsWeaknessResistanceCardEntities : chunk) {
      try {
        setService.getOrCreate(setsWeaknessResistanceCardEntities.getSet());
        for (Weakness weakness : setsWeaknessResistanceCardEntities.getWeaknesses()) {
          weaknessService.getOrCreate(weakness);
        }
        for (Resistance resistance : setsWeaknessResistanceCardEntities.getResistances()) {
          resistanceService.getOrCreate(resistance);
        }
      } catch (Exception e) {
        String formatedItem = looterScrapingErrorHandler.formatErrorItem(
            Constantes.SETS_WEAKNESS_RESISTANCE_CARD_ENTITIES_ITEM,
            setsWeaknessResistanceCardEntities.toString());
        looterScrapingErrorHandler.handle(e, Constantes.SETS_WEAKNESS_RESISTANCE_ENTITIES_WRITER_CONTEXT,
            Constantes.WRITE_ACTION,
            formatedItem);
      }
    }
  }
}
