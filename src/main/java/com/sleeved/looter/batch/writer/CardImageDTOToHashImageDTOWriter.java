package com.sleeved.looter.batch.writer;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.sleeved.looter.common.util.Constantes;
import com.sleeved.looter.infra.dto.CardEntitiesProcessedDTO;
import com.sleeved.looter.infra.dto.HashImageDTO;
import com.sleeved.looter.infra.service.LooterScrapingErrorHandler;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CardImageDTOToHashImageDTOWriter implements ItemWriter<HashImageDTO> {

  private final LooterScrapingErrorHandler looterScrapingErrorHandler;

  public CardImageDTOToHashImageDTOWriter(LooterScrapingErrorHandler looterScrapingErrorHandler) {
    this.looterScrapingErrorHandler = looterScrapingErrorHandler;
  }

  @Override
  public void write(Chunk<? extends HashImageDTO> chunk) throws Exception {
    for (HashImageDTO cardEntities : chunk) {
      try {
        return; // This writer does not perform any action, it is a placeholder for the flow.
      } catch (Exception e) {
        String formatedItem = looterScrapingErrorHandler.formatErrorItem(
            Constantes.CARD_ENTITIES_ITEM,
            cardEntities.toString());
        looterScrapingErrorHandler.handle(e, Constantes.CARD_ENTITIES_WRITER_CONTEXT, Constantes.WRITE_ACTION,
            formatedItem);
      }
    }
  }
}
