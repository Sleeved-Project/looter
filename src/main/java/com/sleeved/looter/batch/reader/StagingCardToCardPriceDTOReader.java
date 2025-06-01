package com.sleeved.looter.batch.reader;

import java.util.Iterator;
import java.util.List;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sleeved.looter.common.util.Constantes;
import com.sleeved.looter.domain.entity.staging.StagingPrice;
import com.sleeved.looter.domain.repository.staging.StagingPriceRepository;
import com.sleeved.looter.infra.dto.CardPriceDTO;
import com.sleeved.looter.infra.service.LooterScrapingErrorHandler;

import lombok.extern.slf4j.Slf4j;

@Component
@StepScope
@Slf4j
public class StagingCardToCardPriceDTOReader implements ItemReader<CardPriceDTO> {
  private Iterator<StagingPrice> cardPriceIterator;
  private ObjectMapper objectMapper;
  private StagingPriceRepository stagingPriceRepository;
  private LooterScrapingErrorHandler looterScrapingErrorHandler;

  public StagingCardToCardPriceDTOReader(StagingPriceRepository stagingPriceRepository, ObjectMapper objectMapper,
      LooterScrapingErrorHandler looterScrapingErrorHandler) {
    this.stagingPriceRepository = stagingPriceRepository;
    this.objectMapper = objectMapper;
    this.looterScrapingErrorHandler = looterScrapingErrorHandler;

    List<StagingPrice> cardPrices = this.stagingPriceRepository.findAll();
    this.cardPriceIterator = cardPrices.iterator();
  }

  @Override
  public CardPriceDTO read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
    if (cardPriceIterator.hasNext()) {
      StagingPrice stagingCardPrice = cardPriceIterator.next();
      try {
        return objectMapper.readValue(stagingCardPrice.getPayload(), CardPriceDTO.class);
      } catch (Exception e) {
        looterScrapingErrorHandler.handle(e, Constantes.STAGING_CARD_PRICE_READER_CONTEXT, Constantes.READER_ACTION,
            Constantes.STAGING_CARD_PRICE_ITEM);
      }
    }
    return null;
  }
}
