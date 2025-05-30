package com.sleeved.looter.batch.writer;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.sleeved.looter.common.util.Constantes;
import com.sleeved.looter.domain.service.*;
import com.sleeved.looter.infra.dto.CardEntitiesProcessedDTO;
import com.sleeved.looter.infra.service.LooterScrapingErrorHandler;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CardWriter implements ItemWriter<CardEntitiesProcessedDTO> {

  private final CardService cardService;
  private final LooterScrapingErrorHandler looterScrapingErrorHandler;

  public CardWriter(CardService cardService, LooterScrapingErrorHandler looterScrapingErrorHandler) {
    this.cardService = cardService;
    this.looterScrapingErrorHandler = looterScrapingErrorHandler;
  }

  @Override
  public void write(Chunk<? extends CardEntitiesProcessedDTO> chunk) throws Exception {
    for (CardEntitiesProcessedDTO cardEntities : chunk) {
      try {
        cardService.getOrCreate(cardEntities.getCard());

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
