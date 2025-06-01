package com.sleeved.looter.batch.writer;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.sleeved.looter.common.util.Constantes;
import com.sleeved.looter.domain.entity.atlas.TcgPlayerPrice;
import com.sleeved.looter.domain.service.TcgPlayerPriceService;
import com.sleeved.looter.infra.dto.TcgPlayerPricesEntitiesProcessedDTO;
import com.sleeved.looter.infra.service.LooterScrapingErrorHandler;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TcgPlayerPricesEntityWriter implements ItemWriter<TcgPlayerPricesEntitiesProcessedDTO> {

  private final TcgPlayerPriceService tcgPlayerPriceService;
  private final LooterScrapingErrorHandler looterScrapingErrorHandler;

  public TcgPlayerPricesEntityWriter(TcgPlayerPriceService tcgPlayerPriceService,
      LooterScrapingErrorHandler looterScrapingErrorHandler) {
    this.tcgPlayerPriceService = tcgPlayerPriceService;
    this.looterScrapingErrorHandler = looterScrapingErrorHandler;
  }

  @Override
  public void write(Chunk<? extends TcgPlayerPricesEntitiesProcessedDTO> chunk) throws Exception {
    for (TcgPlayerPricesEntitiesProcessedDTO tcgPlayerPricesEntities : chunk) {
      try {
        for (TcgPlayerPrice tcgPlayerPrice : tcgPlayerPricesEntities.getTcgPlayerPrices()) {
          tcgPlayerPriceService.getOrCreate(tcgPlayerPrice);
        }
      } catch (Exception e) {
        String formatedItem = looterScrapingErrorHandler.formatErrorItem(
            Constantes.TCGP_PLAYER_PRICE_CARD_ENTITIES_ITEM,
            tcgPlayerPricesEntities.toString());
        looterScrapingErrorHandler.handle(e, Constantes.TCGP_PLAYER_PRICE_CARD_ENTITIES_WRITER_CONTEXT,
            Constantes.WRITE_ACTION,
            formatedItem);
      }
    }
  }
}
