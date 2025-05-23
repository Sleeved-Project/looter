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
import com.sleeved.looter.domain.entity.staging.StagingCard;
import com.sleeved.looter.domain.repository.staging.StagingCardRepository;
import com.sleeved.looter.infra.dto.CardDTO;
import com.sleeved.looter.infra.service.LooterScrapingErrorHandler;

import lombok.extern.slf4j.Slf4j;

@Component
@StepScope
@Slf4j
public class StagingCardToCardDTOReader implements ItemReader<CardDTO> {
  private Iterator<StagingCard> cardIterator;
  private ObjectMapper objectMapper;
  private StagingCardRepository stagingCardRepository;
  private LooterScrapingErrorHandler looterScrapingErrorHandler;

  public StagingCardToCardDTOReader(StagingCardRepository stagingCardRepository, ObjectMapper objectMapper,
      LooterScrapingErrorHandler looterScrapingErrorHandler) {
    this.stagingCardRepository = stagingCardRepository;
    this.objectMapper = objectMapper;
    this.looterScrapingErrorHandler = looterScrapingErrorHandler;

    List<StagingCard> cards = this.stagingCardRepository.findAll();
    this.cardIterator = cards.iterator();
  }

  @Override
  public CardDTO read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
    if (cardIterator.hasNext()) {
      StagingCard stagingCard = cardIterator.next();
      try {
        return objectMapper.readValue(stagingCard.getPayload(), CardDTO.class);
      } catch (Exception e) {
        looterScrapingErrorHandler.handle(e, Constantes.STAGING_CARD_READER_CONTEXT, Constantes.READER_ACTION,
            Constantes.STAGING_CARD_ITEM);
      }
    }
    return null;
  }
}
