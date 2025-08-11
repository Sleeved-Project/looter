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
import com.sleeved.looter.domain.entity.staging.StagingCard;
import com.sleeved.looter.domain.repository.staging.StagingCardRepository;
import com.sleeved.looter.infra.mapper.StagingCardMapper;
import com.sleeved.looter.infra.service.LooterScrapingErrorHandler;
import com.sleeved.looter.infra.service.TcgApiService;
import com.sleeved.looter.infra.service.TcgApiService.PageProcessor;
import com.sleeved.looter.mock.domain.StagingCardMock;

@ExtendWith(MockitoExtension.class)
public class FetchAndStageCardsTaskletTest {
  @Mock
  private LooterScrapingErrorHandler errorHandler;

  @Mock
  private TcgApiService tcgApiService;

  @Mock
  private StagingCardRepository stagingCardRepo;

  @Mock
  private StagingCardMapper stagingCardMapper;

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
  private ArgumentCaptor<List<StagingCard>> stagingCardsCaptor;

  private FetchAndStageCardsTasklet tasklet;

  @BeforeEach
  void setUp() {
    tasklet = new FetchAndStageCardsTasklet(tcgApiService, stagingCardRepo, stagingCardMapper, errorHandler);

    when(chunkContext.getStepContext()).thenReturn(stepContext);
    when(stepContext.getStepExecution()).thenReturn(stepExecution);
    when(stepExecution.getJobExecution()).thenReturn(jobExecution);
    when(jobExecution.getId()).thenReturn(123L);
  }

  @Test
  void execute_shouldProcessCardsPageByPageSuccessfully() throws Exception {
    // Given
    Long expectedJobId = 123L;
    List<JsonNode> mockPageData = StagingCardMock.createMockJsonCardsList(2);
    List<StagingCard> mappedCards = StagingCardMock.createMockStagingCardsList(2);

    when(stagingCardMapper.toEntities(eq(mockPageData), eq(expectedJobId), any(LocalDateTime.class)))
        .thenReturn(mappedCards);

    // When
    RepeatStatus result = tasklet.execute(contribution, chunkContext);

    // Then
    assertThat(result).isEqualTo(RepeatStatus.FINISHED);

    verify(tcgApiService).processAllCardsPageByPage(processorCaptor.capture());

    PageProcessor capturedProcessor = processorCaptor.getValue();
    capturedProcessor.processPage(mockPageData, 1);

    verify(stagingCardMapper).toEntities(eq(mockPageData), eq(expectedJobId), any(LocalDateTime.class));
    verify(stagingCardRepo).saveAll(mappedCards);
  }

  @Test
  void execute_shouldHandleMultiplePagesCorrectly() throws Exception {
    // Given
    Long expectedJobId = 123L;
    List<JsonNode> page1Data = StagingCardMock.createMockJsonCardsList(2);
    List<JsonNode> page2Data = StagingCardMock.createMockJsonCardsList(3);
    List<StagingCard> mappedPage1 = StagingCardMock.createMockStagingCardsList(2);
    List<StagingCard> mappedPage2 = StagingCardMock.createMockStagingCardsList(3);

    when(stagingCardMapper.toEntities(eq(page1Data), eq(expectedJobId), any(LocalDateTime.class)))
        .thenReturn(mappedPage1);
    when(stagingCardMapper.toEntities(eq(page2Data), eq(expectedJobId), any(LocalDateTime.class)))
        .thenReturn(mappedPage2);

    // When
    RepeatStatus result = tasklet.execute(contribution, chunkContext);

    // Then
    assertThat(result).isEqualTo(RepeatStatus.FINISHED);

    verify(tcgApiService).processAllCardsPageByPage(processorCaptor.capture());

    PageProcessor capturedProcessor = processorCaptor.getValue();
    capturedProcessor.processPage(page1Data, 1);
    capturedProcessor.processPage(page2Data, 2);

    verify(stagingCardMapper, times(2)).toEntities(any(), eq(expectedJobId), any(LocalDateTime.class));
    verify(stagingCardRepo, times(2)).saveAll(any());
    verify(stagingCardRepo).saveAll(mappedPage1);
    verify(stagingCardRepo).saveAll(mappedPage2);
  }

  @Test
  void execute_shouldHandleEmptyPageData() throws Exception {
    // Given
    Long expectedJobId = 123L;
    List<JsonNode> emptyPageData = List.of();
    List<StagingCard> emptyMappedCards = List.of();

    when(stagingCardMapper.toEntities(eq(emptyPageData), eq(expectedJobId), any(LocalDateTime.class)))
        .thenReturn(emptyMappedCards);

    // When
    RepeatStatus result = tasklet.execute(contribution, chunkContext);

    // Then
    assertThat(result).isEqualTo(RepeatStatus.FINISHED);

    verify(tcgApiService).processAllCardsPageByPage(processorCaptor.capture());

    PageProcessor capturedProcessor = processorCaptor.getValue();
    capturedProcessor.processPage(emptyPageData, 1);

    verify(stagingCardMapper).toEntities(eq(emptyPageData), eq(expectedJobId), any(LocalDateTime.class));
    verify(stagingCardRepo).saveAll(emptyMappedCards);
  }

  @Test
  void execute_shouldHandleExceptionWithErrorHandler() throws Exception {
    // Given
    RuntimeException apiException = new RuntimeException("API connection failed");
    doThrow(apiException).when(tcgApiService).processAllCardsPageByPage(any(PageProcessor.class));

    // When
    RepeatStatus result = tasklet.execute(contribution, chunkContext);

    // Then
    assertThat(result).isEqualTo(RepeatStatus.FINISHED);

    verify(tcgApiService).processAllCardsPageByPage(any(PageProcessor.class));
    verify(errorHandler).handle(
        eq(apiException),
        eq(Constantes.STAGE_CARD_TASKLET_CONTEXT),
        eq(Constantes.EXECUTE_ACTION),
        eq(Constantes.STAGING_CARD_ITEM));

    verifyNoInteractions(stagingCardMapper, stagingCardRepo);
  }

  @Test
  void execute_shouldHandleProcessorExceptionWithErrorHandler() throws Exception {
    // Given
    Long expectedJobId = 123L;
    List<JsonNode> mockPageData = StagingCardMock.createMockJsonCardsList(2);
    RuntimeException mappingException = new RuntimeException("Mapping failed");

    when(stagingCardMapper.toEntities(eq(mockPageData), eq(expectedJobId), any(LocalDateTime.class)))
        .thenThrow(mappingException);

    // When
    RepeatStatus result = tasklet.execute(contribution, chunkContext);

    // Then
    verify(tcgApiService).processAllCardsPageByPage(processorCaptor.capture());

    PageProcessor capturedProcessor = processorCaptor.getValue();

    assertThatThrownBy(() -> capturedProcessor.processPage(mockPageData, 1))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("Mapping failed");

    // Assert
    assertThat(result).isEqualTo(RepeatStatus.FINISHED);
  }

  @Test
  void execute_shouldUseCorrectJobIdAndTimestamp() throws Exception {
    // Given
    Long expectedJobId = 456L;
    when(jobExecution.getId()).thenReturn(expectedJobId);

    List<JsonNode> mockPageData = StagingCardMock.createMockJsonCardsList(1);
    List<StagingCard> mappedCards = StagingCardMock.createMockStagingCardsList(1);

    when(stagingCardMapper.toEntities(eq(mockPageData), eq(expectedJobId), any(LocalDateTime.class)))
        .thenReturn(mappedCards);

    // When
    tasklet.execute(contribution, chunkContext);

    // Then
    verify(tcgApiService).processAllCardsPageByPage(processorCaptor.capture());

    // Simuler l'appel du processor
    PageProcessor capturedProcessor = processorCaptor.getValue();
    capturedProcessor.processPage(mockPageData, 1);

    verify(stagingCardMapper).toEntities(eq(mockPageData), eq(expectedJobId), any(LocalDateTime.class));
  }

  @Test
  void execute_shouldHandleRepositoryException() throws Exception {
    // Given
    Long expectedJobId = 123L;
    List<JsonNode> mockPageData = StagingCardMock.createMockJsonCardsList(2);
    List<StagingCard> mappedCards = StagingCardMock.createMockStagingCardsList(2);
    RuntimeException repositoryException = new RuntimeException("Database connection failed");

    when(stagingCardMapper.toEntities(eq(mockPageData), eq(expectedJobId), any(LocalDateTime.class)))
        .thenReturn(mappedCards);
    when(stagingCardRepo.saveAll(mappedCards)).thenThrow(repositoryException);

    // When
    RepeatStatus result = tasklet.execute(contribution, chunkContext);

    // Then
    verify(tcgApiService).processAllCardsPageByPage(processorCaptor.capture());
    PageProcessor capturedProcessor = processorCaptor.getValue();

    // Assert
    assertThatThrownBy(() -> capturedProcessor.processPage(mockPageData, 1))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("Database connection failed");
    assertThat(result).isEqualTo(RepeatStatus.FINISHED);
  }
}