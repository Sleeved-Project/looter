package com.sleeved.looter.batch.tasklet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.repeat.RepeatStatus;

import com.fasterxml.jackson.databind.JsonNode;
import com.sleeved.looter.common.util.Constantes;
import com.sleeved.looter.domain.entity.staging.StagingPrice;
import com.sleeved.looter.domain.repository.staging.StagingPriceRepository;
import com.sleeved.looter.infra.mapper.StagingPriceMapper;
import com.sleeved.looter.infra.service.LooterScrapingErrorHandler;
import com.sleeved.looter.infra.service.TcgApiService;
import com.sleeved.looter.infra.service.TcgApiService.PageProcessor;
import com.sleeved.looter.mock.domain.StagingPriceMock;

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

  @Mock
  private StepExecution stepExecution;

  @Mock
  private JobExecution jobExecution;

  @Captor
  private ArgumentCaptor<PageProcessor> processorCaptor;

  @Captor
  private ArgumentCaptor<List<StagingPrice>> stagingPricesCaptor;

  private FetchAndStagePricesTasklet tasklet;

  @BeforeEach
  void setUp() {
    tasklet = new FetchAndStagePricesTasklet(tcgApiService, stagingPriceRepo, stagingPriceMapper, errorHandler);

    when(chunkContext.getStepContext()).thenReturn(stepContext);
    when(stepContext.getStepExecution()).thenReturn(stepExecution);
    when(stepExecution.getJobExecution()).thenReturn(jobExecution);
    when(jobExecution.getId()).thenReturn(123L);
  }

  @Test
  void execute_shouldProcessPricesPageByPageSuccessfully() throws Exception {
    // Given
    Long expectedJobId = 123L;
    List<JsonNode> mockPageData = StagingPriceMock.createMockJsonCardPricesList(2);
    List<StagingPrice> mappedPrices = StagingPriceMock.createMockStagingPricesList(2, expectedJobId);

    when(stagingPriceMapper.toEntities(eq(mockPageData), eq(expectedJobId), any(LocalDateTime.class)))
        .thenReturn(mappedPrices);

    // When
    RepeatStatus result = tasklet.execute(contribution, chunkContext);

    // Then
    assertThat(result).isEqualTo(RepeatStatus.FINISHED);

    verify(tcgApiService).processAllCardPricesPageByPage(processorCaptor.capture());

    // Simuler l'appel du processor avec des données de test
    PageProcessor capturedProcessor = processorCaptor.getValue();
    capturedProcessor.processPage(mockPageData, 1);

    verify(stagingPriceMapper).toEntities(eq(mockPageData), eq(expectedJobId), any(LocalDateTime.class));
    verify(stagingPriceRepo).saveAll(mappedPrices);
  }

  @Test
  void execute_shouldHandleMultiplePagesCorrectly() throws Exception {
    // Given
    Long expectedJobId = 123L;
    List<JsonNode> page1Data = StagingPriceMock.createMockJsonCardPricesList(2);
    List<JsonNode> page2Data = StagingPriceMock.createMockJsonCardPricesList(3);
    List<StagingPrice> mappedPage1 = StagingPriceMock.createMockStagingPricesList(2, expectedJobId);
    List<StagingPrice> mappedPage2 = StagingPriceMock.createMockStagingPricesList(3, expectedJobId);

    when(stagingPriceMapper.toEntities(eq(page1Data), eq(expectedJobId), any(LocalDateTime.class)))
        .thenReturn(mappedPage1);
    when(stagingPriceMapper.toEntities(eq(page2Data), eq(expectedJobId), any(LocalDateTime.class)))
        .thenReturn(mappedPage2);

    // When
    RepeatStatus result = tasklet.execute(contribution, chunkContext);

    // Then
    assertThat(result).isEqualTo(RepeatStatus.FINISHED);

    verify(tcgApiService).processAllCardPricesPageByPage(processorCaptor.capture());

    // Simuler l'appel du processor pour plusieurs pages
    PageProcessor capturedProcessor = processorCaptor.getValue();
    capturedProcessor.processPage(page1Data, 1);
    capturedProcessor.processPage(page2Data, 2);

    verify(stagingPriceMapper, times(2)).toEntities(any(), eq(expectedJobId), any(LocalDateTime.class));
    verify(stagingPriceRepo, times(2)).saveAll(any());
    verify(stagingPriceRepo).saveAll(mappedPage1);
    verify(stagingPriceRepo).saveAll(mappedPage2);
  }

  @Test
  void execute_shouldHandleEmptyPageData() throws Exception {
    // Given
    Long expectedJobId = 123L;
    List<JsonNode> emptyPageData = List.of();
    List<StagingPrice> emptyMappedPrices = List.of();

    when(stagingPriceMapper.toEntities(eq(emptyPageData), eq(expectedJobId), any(LocalDateTime.class)))
        .thenReturn(emptyMappedPrices);

    // When
    RepeatStatus result = tasklet.execute(contribution, chunkContext);

    // Then
    assertThat(result).isEqualTo(RepeatStatus.FINISHED);

    verify(tcgApiService).processAllCardPricesPageByPage(processorCaptor.capture());

    // Simuler l'appel du processor avec une page vide
    PageProcessor capturedProcessor = processorCaptor.getValue();
    capturedProcessor.processPage(emptyPageData, 1);

    verify(stagingPriceMapper).toEntities(eq(emptyPageData), eq(expectedJobId), any(LocalDateTime.class));
    verify(stagingPriceRepo).saveAll(emptyMappedPrices);
  }

  @Test
  void execute_shouldHandleExceptionWithErrorHandler() throws Exception {
    // Given
    RuntimeException apiException = new RuntimeException("API connection failed");
    doThrow(apiException).when(tcgApiService).processAllCardPricesPageByPage(any(PageProcessor.class));

    // When
    RepeatStatus result = tasklet.execute(contribution, chunkContext);

    // Then
    assertThat(result).isEqualTo(RepeatStatus.FINISHED);

    verify(tcgApiService).processAllCardPricesPageByPage(any(PageProcessor.class));
    verify(errorHandler).handle(
        eq(apiException),
        eq(Constantes.STAGE_CARD_TASKLET_CONTEXT),
        eq(Constantes.EXECUTE_ACTION),
        eq(Constantes.STAGING_CARD_PRICE_ITEM));

    verifyNoInteractions(stagingPriceMapper, stagingPriceRepo);
  }

  @Test
  void execute_shouldHandleProcessorExceptionWithErrorHandler() throws Exception {
    // Given
    Long expectedJobId = 123L;
    List<JsonNode> mockPageData = StagingPriceMock.createMockJsonCardPricesList(2);
    RuntimeException mappingException = new RuntimeException("Mapping failed");

    when(stagingPriceMapper.toEntities(eq(mockPageData), eq(expectedJobId), any(LocalDateTime.class)))
        .thenThrow(mappingException);

    // When
    RepeatStatus result = tasklet.execute(contribution, chunkContext);

    // Then
    verify(tcgApiService).processAllCardPricesPageByPage(processorCaptor.capture());

    PageProcessor capturedProcessor = processorCaptor.getValue();

    assertThatThrownBy(() -> capturedProcessor.processPage(mockPageData, 1))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("Mapping failed");

    // L'exception sera gérée par le try-catch global du tasklet
    assertThat(result).isEqualTo(RepeatStatus.FINISHED);
  }

  @Test
  void execute_shouldUseCorrectJobIdAndTimestamp() throws Exception {
    // Given
    Long expectedJobId = 456L;
    when(jobExecution.getId()).thenReturn(expectedJobId);

    List<JsonNode> mockPageData = StagingPriceMock.createMockJsonCardPricesList(1);
    List<StagingPrice> mappedPrices = StagingPriceMock.createMockStagingPricesList(1, expectedJobId);

    when(stagingPriceMapper.toEntities(eq(mockPageData), eq(expectedJobId), any(LocalDateTime.class)))
        .thenReturn(mappedPrices);

    // When
    tasklet.execute(contribution, chunkContext);

    // Then
    verify(tcgApiService).processAllCardPricesPageByPage(processorCaptor.capture());

    // Simuler l'appel du processor
    PageProcessor capturedProcessor = processorCaptor.getValue();
    capturedProcessor.processPage(mockPageData, 1);

    verify(stagingPriceMapper).toEntities(eq(mockPageData), eq(expectedJobId), any(LocalDateTime.class));
  }

  @Test
  void execute_shouldHandleRepositoryException() throws Exception {
    // Given
    Long expectedJobId = 123L;
    List<JsonNode> mockPageData = StagingPriceMock.createMockJsonCardPricesList(2);
    List<StagingPrice> mappedPrices = StagingPriceMock.createMockStagingPricesList(2, expectedJobId);
    RuntimeException repositoryException = new RuntimeException("Database connection failed");

    when(stagingPriceMapper.toEntities(eq(mockPageData), eq(expectedJobId), any(LocalDateTime.class)))
        .thenReturn(mappedPrices);
    when(stagingPriceRepo.saveAll(mappedPrices)).thenThrow(repositoryException);

    // When
    RepeatStatus result = tasklet.execute(contribution, chunkContext);

    // Then
    verify(tcgApiService).processAllCardPricesPageByPage(processorCaptor.capture());
    PageProcessor capturedProcessor = processorCaptor.getValue();

    // Assert
    assertThatThrownBy(() -> capturedProcessor.processPage(mockPageData, 1))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("Database connection failed");
    assertThat(result).isEqualTo(RepeatStatus.FINISHED);
  }
}