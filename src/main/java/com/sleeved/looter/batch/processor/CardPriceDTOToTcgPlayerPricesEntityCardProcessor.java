package com.sleeved.looter.batch.processor;

import java.util.List;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.sleeved.looter.common.util.Constantes;
import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.entity.atlas.TcgPlayerPrice;
import com.sleeved.looter.domain.entity.atlas.TcgPlayerReporting;
import com.sleeved.looter.domain.service.CardService;
import com.sleeved.looter.domain.service.TcgPlayerReportingService;
import com.sleeved.looter.infra.dto.CardPriceDTO;
import com.sleeved.looter.infra.dto.TcgPlayerPricesEntitiesProcessedDTO;
import com.sleeved.looter.infra.mapper.*;
import com.sleeved.looter.infra.processor.TcgPlayerPriceProcessor;
import com.sleeved.looter.infra.service.LooterScrapingErrorHandler;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CardPriceDTOToTcgPlayerPricesEntityCardProcessor
    implements ItemProcessor<CardPriceDTO, TcgPlayerPricesEntitiesProcessedDTO> {

  private final TcgPlayerPriceProcessor tcgPlayerPriceProcessor;

  private final TcgPlayerReportingService tcgPlayerReportingService;

  private final TcgPlayerReportingMapper tcgPlayerReportingMapper;

  private final CardService cardService;

  private final LooterScrapingErrorHandler looterScrapingErrorHandler;

  public CardPriceDTOToTcgPlayerPricesEntityCardProcessor(LooterScrapingErrorHandler looterScrapingErrorHandler,
      CardService cardService, TcgPlayerReportingMapper tcgPlayerReportingMapper,
      TcgPlayerReportingService tcgPlayerReportingService, TcgPlayerPriceProcessor tcgPlayerPriceProcessor) {
    this.cardService = cardService;
    this.tcgPlayerReportingMapper = tcgPlayerReportingMapper;
    this.tcgPlayerReportingService = tcgPlayerReportingService;
    this.tcgPlayerPriceProcessor = tcgPlayerPriceProcessor;
    this.looterScrapingErrorHandler = looterScrapingErrorHandler;
  }

  @Override
  public TcgPlayerPricesEntitiesProcessedDTO process(CardPriceDTO item) {
    try {
      Card card = cardService.getById(item.getId());

      TcgPlayerReporting tcgPlayerReportingToFind = tcgPlayerReportingMapper.toEntity(item.getTcgplayer(), card);

      if (tcgPlayerReportingToFind == null) {
        return null;
      }

      TcgPlayerReporting tcgPlayerReportingFound = tcgPlayerReportingService
          .getByUpdatedAtAndCard(tcgPlayerReportingToFind);

      List<TcgPlayerPrice> tcgPlayerPrices = tcgPlayerPriceProcessor.processFromDTOs(item.getTcgplayer().getPrices(),
          tcgPlayerReportingFound);

      if (tcgPlayerPrices == null || tcgPlayerPrices.isEmpty()) {
        return null;
      }

      TcgPlayerPricesEntitiesProcessedDTO tcgPlayerPricesEntitiesProcessedDTO = new TcgPlayerPricesEntitiesProcessedDTO();
      tcgPlayerPricesEntitiesProcessedDTO.setTcgPlayerPrices(tcgPlayerPrices);

      return tcgPlayerPricesEntitiesProcessedDTO;
    } catch (Exception e) {
      String formatedItem = looterScrapingErrorHandler.formatErrorItem(
          Constantes.CARD_PRICE_DTO_ITEM,
          item.toString());
      looterScrapingErrorHandler.handle(e, Constantes.CARD_DTO_TO_TCG_PLAYER_PRICE_ENTITY_PROCESSOR_CONTEXT,
          Constantes.PROCESSOR_ACTION,
          formatedItem);
      return null;
    }

  }

}
