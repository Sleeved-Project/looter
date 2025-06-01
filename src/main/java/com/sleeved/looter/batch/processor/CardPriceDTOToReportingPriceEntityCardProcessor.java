package com.sleeved.looter.batch.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.sleeved.looter.common.util.Constantes;
import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.entity.atlas.CardMarketPrice;
import com.sleeved.looter.domain.entity.atlas.TcgPlayerReporting;
import com.sleeved.looter.domain.service.CardService;
import com.sleeved.looter.infra.dto.CardPriceDTO;
import com.sleeved.looter.infra.dto.ReportingPriceEntitiesProcessedDTO;
import com.sleeved.looter.infra.mapper.*;
import com.sleeved.looter.infra.service.LooterScrapingErrorHandler;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CardPriceDTOToReportingPriceEntityCardProcessor
    implements ItemProcessor<CardPriceDTO, ReportingPriceEntitiesProcessedDTO> {

  private final CardMarketPriceMapper cardMarketPriceMapper;

  private final TcgPlayerReportingMapper tcgPlayerReportingMapper;

  private final CardService cardService;

  private final LooterScrapingErrorHandler looterScrapingErrorHandler;

  public CardPriceDTOToReportingPriceEntityCardProcessor(LooterScrapingErrorHandler looterScrapingErrorHandler,
      CardService cardService, TcgPlayerReportingMapper tcgPlayerReportingMapper,
      CardMarketPriceMapper cardMarketPriceMapper) {
    this.cardService = cardService;
    this.tcgPlayerReportingMapper = tcgPlayerReportingMapper;
    this.cardMarketPriceMapper = cardMarketPriceMapper;
    this.looterScrapingErrorHandler = looterScrapingErrorHandler;
  }

  @Override
  public ReportingPriceEntitiesProcessedDTO process(CardPriceDTO item) {
    try {
      Card card = cardService.getById(item.getId());

      TcgPlayerReporting tcgPlayerReporting = tcgPlayerReportingMapper.toEntity(item.getTcgplayer(), card);
      CardMarketPrice cardMarketPrice = cardMarketPriceMapper.toEntity(item.getCardmarket(), card);

      if (tcgPlayerReporting == null && cardMarketPrice == null) {
        return null;
      }

      ReportingPriceEntitiesProcessedDTO reportingPriceEntitiesProcessedDTO = new ReportingPriceEntitiesProcessedDTO();
      reportingPriceEntitiesProcessedDTO.setTcgPlayerReporting(tcgPlayerReporting);
      reportingPriceEntitiesProcessedDTO.setCardMarketPrice(cardMarketPrice);

      return reportingPriceEntitiesProcessedDTO;
    } catch (Exception e) {
      String formatedItem = looterScrapingErrorHandler.formatErrorItem(
          Constantes.CARD_PRICE_DTO_ITEM,
          item.toString());
      looterScrapingErrorHandler.handle(e, Constantes.CARD_DTO_TO_REPORTING_PRICE_ENTITY_PROCESSOR_CONTEXT,
          Constantes.PROCESSOR_ACTION,
          formatedItem);
      return null;
    }

  }

}
