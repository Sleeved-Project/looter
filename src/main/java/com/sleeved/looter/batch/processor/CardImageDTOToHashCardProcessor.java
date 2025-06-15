package com.sleeved.looter.batch.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.sleeved.looter.common.util.Constantes;
import com.sleeved.looter.domain.entity.iris.HashCard;
import com.sleeved.looter.domain.repository.iris.HashCardRepository;
import com.sleeved.looter.infra.dto.CardImageDTO;
import com.sleeved.looter.infra.mapper.HashImageMapper;
import com.sleeved.looter.infra.service.IrisApiService;
import com.sleeved.looter.infra.service.LooterScrapingErrorHandler;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CardImageDTOToHashCardProcessor
    implements ItemProcessor<CardImageDTO, HashCard> {

  private final IrisApiService irisApiService;
  private final LooterScrapingErrorHandler looterScrapingErrorHandler;
  private final HashImageMapper hashImageMapper;
  private final HashCardRepository hashCardRepository;

  public CardImageDTOToHashCardProcessor(
      LooterScrapingErrorHandler looterScrapingErrorHandler,
      IrisApiService irisApiService,
      HashImageMapper hashImageMapper,
      HashCardRepository hashCardRepository) {
    this.irisApiService = irisApiService;
    this.looterScrapingErrorHandler = looterScrapingErrorHandler;
    this.hashImageMapper = hashImageMapper;
    this.hashCardRepository = hashCardRepository;
  }

  @Override
  public HashCard process(CardImageDTO item) {
    if (item == null || item.getImageUrl() == null || item.getImageUrl().isEmpty()) {
      return null;
    }

    // Check if the hash already exists in the repository
    HashCard existingHashCard = hashCardRepository.findById(item.getCardId()).orElse(null);
    if (existingHashCard != null) {
      log.debug("HashCard with ID {} already exists, skipping processing.", item.getCardId());
      return null;
    }
    
    try {
      JsonNode response = irisApiService.fetchHashImage(item.getImageUrl());
      return hashImageMapper.toHashCard(item, response);

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
