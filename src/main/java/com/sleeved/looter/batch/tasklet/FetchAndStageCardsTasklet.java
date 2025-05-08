package com.sleeved.looter.batch.tasklet;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.sleeved.looter.domain.entity.staging.StagingCard;
import com.sleeved.looter.domain.repository.staging.StagingCardRepository;
import com.sleeved.looter.infra.service.TcgApiService;

@Component
public class FetchAndStageCardsTasklet implements Tasklet {

  private final TcgApiService tcgApiService;
  private final StagingCardRepository stagingCardRepo;

  public FetchAndStageCardsTasklet(TcgApiService tcgApiService, StagingCardRepository stagingCardRepo) {
    this.tcgApiService = tcgApiService;
    this.stagingCardRepo = stagingCardRepo;
  }

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
    Long jobId = chunkContext.getStepContext().getStepExecution().getJobExecution().getId();
    LocalDateTime now = LocalDateTime.now();

    List<JsonNode> cards = tcgApiService.fetchAllCards();

    List<StagingCard> entities = cards.stream()
        .map(cardJson -> {
          StagingCard entity = new StagingCard();
          entity.setId(cardJson.path("id").asText());
          entity.setPayload(cardJson.toString());
          entity.setUpdatedAt(now);
          entity.setBatchId(jobId);
          return entity;
        })
        .collect(Collectors.toList());

    stagingCardRepo.saveAll(entities);

    return RepeatStatus.FINISHED;
  }
}
