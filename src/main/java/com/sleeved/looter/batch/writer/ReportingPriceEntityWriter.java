package com.sleeved.looter.batch.writer;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.sleeved.looter.common.util.Constantes;
import com.sleeved.looter.domain.service.*;
import com.sleeved.looter.infra.dto.ReportingPriceEntitiesProcessedDTO;
import com.sleeved.looter.infra.service.LooterScrapingErrorHandler;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ReportingPriceEntityWriter implements ItemWriter<ReportingPriceEntitiesProcessedDTO> {

  private final TcgPlayerReportingService tcgPlayerReportingService;

  private final CardMarketPriceService cardMarketPriceService;
  private final LooterScrapingErrorHandler looterScrapingErrorHandler;

  public ReportingPriceEntityWriter(SetService setService, WeaknessService weaknessService,
      ResistanceService resistanceService,
      LooterScrapingErrorHandler looterScrapingErrorHandler, CardMarketPriceService cardMarketPriceService,
      TcgPlayerReportingService tcgPlayerReportingService) {
    this.cardMarketPriceService = cardMarketPriceService;
    this.tcgPlayerReportingService = tcgPlayerReportingService;
    this.looterScrapingErrorHandler = looterScrapingErrorHandler;
  }

  @Override
  public void write(Chunk<? extends ReportingPriceEntitiesProcessedDTO> chunk) throws Exception {
    for (ReportingPriceEntitiesProcessedDTO reportingPriceEntities : chunk) {
      try {
        if (reportingPriceEntities == null) {
          continue;
        }
        if (reportingPriceEntities.getCardMarketPrice() != null) {
          cardMarketPriceService.getOrCreate(reportingPriceEntities.getCardMarketPrice());
        }
        if (reportingPriceEntities.getTcgPlayerReporting() != null) {
          tcgPlayerReportingService.getOrCreate(reportingPriceEntities.getTcgPlayerReporting());
        }
      } catch (Exception e) {
        String formatedItem = looterScrapingErrorHandler.formatErrorItem(
            Constantes.REPORTING_PRICE_ITEM,
            reportingPriceEntities.toString());
        looterScrapingErrorHandler.handle(e, Constantes.REPORTING_PRICE_ENTITIES_WRITER_CONTEXT,
            Constantes.WRITE_ACTION,
            formatedItem);
      }
    }
  }
}
