package com.sleeved.looter.batch.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.sleeved.looter.common.util.Constantes;
import com.sleeved.looter.infra.dto.CardImageDTO;
import com.sleeved.looter.infra.dto.HashImageDTO;
import com.sleeved.looter.infra.mapper.HashImageMapper;
import com.sleeved.looter.infra.service.IrisApiService;
import com.sleeved.looter.infra.service.LooterScrapingErrorHandler;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CardImageDTOToHashImageDTOProcessor
    implements ItemProcessor<CardImageDTO, HashImageDTO> {

  private final IrisApiService irisApiService;
  private final LooterScrapingErrorHandler looterScrapingErrorHandler;
  private final HashImageMapper hashImageMapper;

  public CardImageDTOToHashImageDTOProcessor(
      LooterScrapingErrorHandler looterScrapingErrorHandler,
      IrisApiService irisApiService,
      HashImageMapper hashImageMapper) {
    this.irisApiService = irisApiService;
    this.looterScrapingErrorHandler = looterScrapingErrorHandler;
    this.hashImageMapper = hashImageMapper;
  }

  @Override
  public HashImageDTO process(CardImageDTO item) {
    try {
      log.info("Processing CardImageDTO to HashImageDTO: {}", item);
      JsonNode response = irisApiService.fetchHashImage(item.getImageUrl());
      return hashImageMapper.toHashImageDTO(item, response);

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
