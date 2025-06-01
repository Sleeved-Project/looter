package com.sleeved.looter.batch.tasklet;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.sleeved.looter.common.util.Constantes;
import com.sleeved.looter.domain.entity.staging.StagingPrice;
import com.sleeved.looter.domain.repository.staging.StagingPriceRepository;
import com.sleeved.looter.infra.mapper.StagingPriceMapper;
import com.sleeved.looter.infra.service.LooterScrapingErrorHandler;
import com.sleeved.looter.infra.service.TcgApiService;

@Component
public class FetchAndStagePricesTasklet implements Tasklet {

  private final LooterScrapingErrorHandler looterScrapingErrorHandler;

  private final TcgApiService tcgApiService;
  private final StagingPriceRepository stagingPriceRepo;
  private final StagingPriceMapper stagingPriceMapper;

  public FetchAndStagePricesTasklet(TcgApiService tcgApiService, StagingPriceRepository stagingPriceRepo,
      StagingPriceMapper stagingPriceMapper, LooterScrapingErrorHandler looterScrapingErrorHandler) {
    this.tcgApiService = tcgApiService;
    this.stagingPriceRepo = stagingPriceRepo;
    this.stagingPriceMapper = stagingPriceMapper;
    this.looterScrapingErrorHandler = looterScrapingErrorHandler;
  }

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
    try {
      Long jobId = chunkContext.getStepContext().getStepExecution().getJobExecution().getId();
      LocalDateTime now = LocalDateTime.now();

      List<JsonNode> cardPrices = tcgApiService.fetchAllCardPrices();

      List<StagingPrice> entities = stagingPriceMapper.toEntities(cardPrices, jobId, now);

      stagingPriceRepo.saveAll(entities);

      return RepeatStatus.FINISHED;
    } catch (Exception e) {
      looterScrapingErrorHandler.handle(e, Constantes.STAGE_CARD_TASKLET_CONTEXT, Constantes.EXECUTE_ACTION,
          Constantes.STAGING_CARD_PRICE_ITEM);
      return RepeatStatus.FINISHED;
    }
  }
}
