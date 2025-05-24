package com.sleeved.looter.infra.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.sleeved.looter.domain.entity.staging.StagingCard;
import com.sleeved.looter.mock.domain.StagingCardMock;

public class StagingCardMapperTest {

  private StagingCardMapper mapper;
  private LocalDateTime fixedNow;
  private Long fixedBatchId;

  @BeforeEach
  void setUp() {
    mapper = new StagingCardMapper();
    fixedNow = LocalDateTime.of(2025, 5, 11, 14, 30);
    fixedBatchId = 123L;
  }

  @Test
  void toEntity_shouldMapCardJsonToStagingCard() {
    // Given
    JsonNode cardJson = StagingCardMock.createMockJsonCard("card1", "Test Card");

    // When
    StagingCard result = mapper.toEntity(cardJson, fixedBatchId, fixedNow);

    // Then∂
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo("card1");
    assertThat(result.getPayload()).contains("Test Card");
    assertThat(result.getBatchId()).isEqualTo(fixedBatchId);
    assertThat(result.getUpdatedAt()).isEqualTo(fixedNow);
  }

  @Test
  void toEntities_shouldMapMultipleCardsToStagingCards() {
    // Given
    List<JsonNode> cards = StagingCardMock.createMockJsonCardsList(2);

    // When
    List<StagingCard> results = mapper.toEntities(cards, fixedBatchId, fixedNow);

    // Then
    assertThat(results).hasSize(2);

    // Vérifier le premier élément
    assertThat(results.get(0).getId()).isEqualTo("card0");
    assertThat(results.get(0).getPayload()).contains("Test Card 0");
    assertThat(results.get(0).getBatchId()).isEqualTo(fixedBatchId);
    assertThat(results.get(0).getUpdatedAt()).isEqualTo(fixedNow);

    // Vérifier le second élément
    assertThat(results.get(1).getId()).isEqualTo("card1");
    assertThat(results.get(1).getPayload()).contains("Test Card 1");
    assertThat(results.get(1).getBatchId()).isEqualTo(fixedBatchId);
    assertThat(results.get(1).getUpdatedAt()).isEqualTo(fixedNow);
  }

  @Test
  void toEntities_shouldHandleEmptyList() {
    // Given
    List<JsonNode> emptyList = StagingCardMock.createMockJsonCardsList(0);

    // When
    List<StagingCard> results = mapper.toEntities(emptyList, fixedBatchId, fixedNow);

    // Then
    assertThat(results).isEmpty();
  }
}
