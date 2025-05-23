package com.sleeved.looter.batch.tasklet;

import com.fasterxml.jackson.databind.JsonNode;
import com.sleeved.looter.domain.entity.staging.StagingCard;
import com.sleeved.looter.domain.repository.staging.StagingCardRepository;
import com.sleeved.looter.infra.mapper.StagingCardMapper;
import com.sleeved.looter.infra.service.TcgApiService;
import com.sleeved.looter.mock.StagingCardMock;

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
public class FetchAndStageCardsTaskletTest {

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
    tasklet = new FetchAndStageCardsTasklet(tcgApiService, stagingCardRepo, stagingCardMapper);

    // Configuration du context pour simuler un JobId
    StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution();
    when(chunkContext.getStepContext()).thenReturn(stepContext);
    when(stepContext.getStepExecution()).thenReturn(stepExecution);
  }

  @Test
  void execute_shouldFollowCompleteWorkflow() throws Exception {
    // Given
    Long expectedJobId = 123L;

    List<JsonNode> mockCards = StagingCardMock.createMockJsonCardsList(2);
    List<StagingCard> mappedCards = StagingCardMock.createMockStagingCardsList(2);

    // When
    when(tcgApiService.fetchAllCards()).thenReturn(mockCards);
    when(stagingCardMapper.toEntities(eq(mockCards), eq(expectedJobId), any(LocalDateTime.class)))
        .thenReturn(mappedCards);

    // Then
    RepeatStatus result = tasklet.execute(contribution, chunkContext);

    // Verify
    assertThat(result).isEqualTo(RepeatStatus.FINISHED);

    verify(tcgApiService).fetchAllCards();
    verify(stagingCardMapper).toEntities(eq(mockCards), eq(expectedJobId), any(LocalDateTime.class));
    verify(stagingCardRepo).saveAll(mappedCards);
  }

  @Test
  void execute_shouldHandleEmptyCardsList() throws Exception {
    // Given
    Long expectedJobId = 123L;
    List<JsonNode> emptyCards = List.of();
    List<StagingCard> emptyMappedCards = List.of();

    when(tcgApiService.fetchAllCards()).thenReturn(emptyCards);
    when(stagingCardMapper.toEntities(eq(emptyCards), eq(expectedJobId), any(LocalDateTime.class)))
        .thenReturn(emptyMappedCards);

    // When
    RepeatStatus result = tasklet.execute(contribution, chunkContext);

    // Then
    assertThat(result).isEqualTo(RepeatStatus.FINISHED);
    verify(stagingCardRepo).saveAll(emptyMappedCards);
  }
}