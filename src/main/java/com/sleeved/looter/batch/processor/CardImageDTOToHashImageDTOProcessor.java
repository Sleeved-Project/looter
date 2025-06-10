package com.sleeved.looter.batch.processor;


import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.sleeved.looter.common.util.Constantes;

import com.sleeved.looter.infra.dto.CardImageDTO;
import com.sleeved.looter.infra.dto.HashImageDTO;

import com.sleeved.looter.infra.service.LooterScrapingErrorHandler;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CardImageDTOToHashImageDTOProcessor
    implements ItemProcessor<CardImageDTO, HashImageDTO> {

  private final LooterScrapingErrorHandler looterScrapingErrorHandler;


  public CardImageDTOToHashImageDTOProcessor(
      LooterScrapingErrorHandler looterScrapingErrorHandler) {
    this.looterScrapingErrorHandler = looterScrapingErrorHandler;
  }

  @Override
  public HashImageDTO process(CardImageDTO item) {
    try {
        return new HashImageDTO();
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
