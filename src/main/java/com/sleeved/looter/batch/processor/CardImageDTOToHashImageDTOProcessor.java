package com.sleeved.looter.batch.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.sleeved.looter.common.util.Constantes;
import com.sleeved.looter.infra.dto.CardImageDTO;
import com.sleeved.looter.infra.dto.HashImageDTO;
import com.sleeved.looter.infra.service.IrisApiService;
import com.sleeved.looter.infra.service.LooterScrapingErrorHandler;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CardImageDTOToHashImageDTOProcessor
    implements ItemProcessor<CardImageDTO, HashImageDTO> {

  private final IrisApiService irisApiService;
  private final LooterScrapingErrorHandler looterScrapingErrorHandler;

  public CardImageDTOToHashImageDTOProcessor(
      LooterScrapingErrorHandler looterScrapingErrorHandler,
      IrisApiService irisApiService) {
    this.irisApiService = irisApiService;
    this.looterScrapingErrorHandler = looterScrapingErrorHandler;
  }

  @Override
  public HashImageDTO process(CardImageDTO item) {
    if (item == null || item.getImageUrl() == null || item.getImageUrl().isEmpty()) {
      return null;
    }
    
    try {
      String hash = irisApiService.fetchHashImage(item.getImageUrl());
      
      if (hash == null || hash.isEmpty()) {
        return null;
      }
      HashImageDTO hashImageDTO = new HashImageDTO();
      hashImageDTO.setId(item.getCardId());
      hashImageDTO.setHash(hash);
      return hashImageDTO;

    } catch (Exception e) {
      String formatedItem = looterScrapingErrorHandler.formatErrorItem(
          Constantes.CARD_DTO_ITEM,
          item.toString());
      looterScrapingErrorHandler.handle(e,
          Constantes.CARD_IMAGE_TO_HASH_IMAGE_PROCESSOR_CONTEXT,
          Constantes.PROCESSOR_ACTION,
          formatedItem);
      return null;
    }
  }
}
