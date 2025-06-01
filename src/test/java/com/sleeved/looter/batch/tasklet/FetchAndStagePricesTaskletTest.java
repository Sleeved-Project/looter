package com.sleeved.looter.batch.tasklet;

import com.fasterxml.jackson.databind.JsonNode;
import com.sleeved.looter.common.util.Constantes;
import com.sleeved.looter.domain.entity.staging.StagingPrice;
import com.sleeved.looter.domain.repository.staging.StagingPriceRepository;
import com.sleeved.looter.infra.mapper.StagingPriceMapper;
import com.sleeved.looter.infra.service.LooterScrapingErrorHandler;
import com.sleeved.looter.infra.service.TcgApiService;
import com.sleeved.looter.mock.domain.StagingPriceMock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.test.MetaDataInstanceFactory;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FetchAndStagePricesTaskletTest {
  @Mock
  private LooterScrapingErrorHandler errorHandler;

  @Mock
  private TcgApiService tcgApiService;

  @Mock
  private StagingPriceRepository stagingPriceRepo;

  @Mock
  private StagingPriceMapper stagingPriceMapper;

  @Mock
  private StepContribution contribution;

  @Mock
  private ChunkContext chunkContext;

  @Mock
  private StepContext stepContext;

  @Captor
  private ArgumentCaptor<List<StagingPrice>> stagingPricesCaptor;

  private FetchAndStagePricesTasklet tasklet;

  @BeforeEach
  void setUp() {
    tasklet = new FetchAndStagePricesTasklet(tcgApiService, stagingPriceRepo, stagingPriceMapper, errorHandler);

    StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution();
    when(chunkContext.getStepContext()).thenReturn(stepContext);
    when(stepContext.getStepExecution()).thenReturn(stepExecution);
  }

  @Test
  void execute_shouldFollowCompleteWorkflow() throws Exception {
    Long expectedJobId = 123L;

    List<JsonNode> mockPrices = StagingPriceMock.createMockJsonCardPricesList(2);
    List<StagingPrice> mappedPrices = StagingPriceMock.createMockStagingPricesList(2, expectedJobId);

    when(tcgApiService.fetchAllCardPrices()).thenReturn(mockPrices);
    when(stagingPriceMapper.toEntities(eq(mockPrices), eq(expectedJobId), any(LocalDateTime.class)))
        .thenReturn(mappedPrices);

    RepeatStatus result = tasklet.execute(contribution, chunkContext);

    assertThat(result).isEqualTo(RepeatStatus.FINISHED);

    verify(tcgApiService).fetchAllCardPrices();
    verify(stagingPriceMapper).toEntities(eq(mockPrices), eq(expectedJobId), any(LocalDateTime.class));
    verify(stagingPriceRepo).saveAll(mappedPrices);
  }

  @Test
  void execute_shouldHandleEmptyPricesList() throws Exception {
    Long expectedJobId = 123L;
    List<JsonNode> emptyPrices = List.of();
    List<StagingPrice> emptyMappedPrices = List.of();

    when(tcgApiService.fetchAllCardPrices()).thenReturn(emptyPrices);
    when(stagingPriceMapper.toEntities(eq(emptyPrices), eq(expectedJobId), any(LocalDateTime.class)))
        .thenReturn(emptyMappedPrices);

    RepeatStatus result = tasklet.execute(contribution, chunkContext);

    assertThat(result).isEqualTo(RepeatStatus.FINISHED);
    verify(stagingPriceRepo).saveAll(emptyMappedPrices);
  }

  @Test
  void execute_shouldHandleErrors() throws Exception {
    RuntimeException apiException = new RuntimeException("API error");
    when(tcgApiService.fetchAllCardPrices()).thenThrow(apiException);

    RepeatStatus result = tasklet.execute(contribution, chunkContext);

    assertThat(result).isEqualTo(RepeatStatus.FINISHED);
    verify(errorHandler).handle(
        eq(apiException),
        eq(Constantes.STAGE_CARD_TASKLET_CONTEXT),
        eq(Constantes.EXECUTE_ACTION),
        eq(Constantes.STAGING_CARD_PRICE_ITEM));
    verifyNoInteractions(stagingPriceMapper, stagingPriceRepo);
  }
}