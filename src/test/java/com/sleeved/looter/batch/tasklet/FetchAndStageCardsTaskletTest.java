package com.sleeved.looter.batch.tasklet;

import com.fasterxml.jackson.databind.JsonNode;
import com.sleeved.looter.common.exception.LooterScrapingException;
import com.sleeved.looter.domain.entity.staging.StagingCard;
import com.sleeved.looter.domain.repository.staging.StagingCardRepository;
import com.sleeved.looter.infra.mapper.StagingCardMapper;
import com.sleeved.looter.infra.service.LooterScrapingErrorHandler;
import com.sleeved.looter.infra.service.TcgApiService;
import com.sleeved.looter.mock.domain.StagingCardMock;

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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

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

  @Captor
  private ArgumentCaptor<List<StagingCard>> stagingCardsCaptor;

  private FetchAndStageCardsTasklet tasklet;

  @BeforeEach
  void setUp() {
    tasklet = new FetchAndStageCardsTasklet(tcgApiService, stagingCardRepo, stagingCardMapper, errorHandler);

    StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution();
    when(chunkContext.getStepContext()).thenReturn(stepContext);
    when(stepContext.getStepExecution()).thenReturn(stepExecution);
  }

  @Test
  void execute_shouldFollowCompleteWorkflow() throws Exception {
    Long expectedJobId = 123L;

    List<JsonNode> mockCards = StagingCardMock.createMockJsonCardsList(2);
    List<StagingCard> mappedCards = StagingCardMock.createMockStagingCardsList(2);

    when(tcgApiService.fetchAllCards()).thenReturn(mockCards);
    when(stagingCardMapper.toEntities(eq(mockCards), eq(expectedJobId), any(LocalDateTime.class)))
        .thenReturn(mappedCards);

    RepeatStatus result = tasklet.execute(contribution, chunkContext);

    assertThat(result).isEqualTo(RepeatStatus.FINISHED);

    verify(tcgApiService).fetchAllCards();
    verify(stagingCardMapper).toEntities(eq(mockCards), eq(expectedJobId), any(LocalDateTime.class));
    verify(stagingCardRepo).saveAll(mappedCards);
  }

  @Test
  void execute_shouldHandleEmptyCardsList() throws Exception {
    Long expectedJobId = 123L;
    List<JsonNode> emptyCards = List.of();
    List<StagingCard> emptyMappedCards = List.of();

    when(tcgApiService.fetchAllCards()).thenReturn(emptyCards);
    when(stagingCardMapper.toEntities(eq(emptyCards), eq(expectedJobId), any(LocalDateTime.class)))
        .thenReturn(emptyMappedCards);

    RepeatStatus result = tasklet.execute(contribution, chunkContext);

    assertThat(result).isEqualTo(RepeatStatus.FINISHED);
    verify(stagingCardRepo).saveAll(emptyMappedCards);
  }

  @Test
  void execute_shouldHandleExceptionWithErrorHandler() throws Exception {
    Exception apiException = new RuntimeException("API error");
    when(tcgApiService.fetchAllCards()).thenThrow(apiException);

    LooterScrapingException expectedException = new LooterScrapingException("Error fetching cards", apiException);
    doThrow(expectedException).when(errorHandler).handle(
        any(Exception.class),
        anyString(),
        anyString(),
        anyString());

    assertThatThrownBy(() -> tasklet.execute(contribution, chunkContext))
        .isInstanceOf(LooterScrapingException.class);

    verify(tcgApiService).fetchAllCards();
    verifyNoInteractions(stagingCardMapper, stagingCardRepo);
  }
}