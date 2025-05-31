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
    JsonNode cardJson = StagingCardMock.createMockJsonCard("card1", "Test Card");

    StagingCard result = mapper.toEntity(cardJson, fixedBatchId, fixedNow);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo("card1");
    assertThat(result.getPayload()).contains("Test Card");
    assertThat(result.getBatchId()).isEqualTo(fixedBatchId);
    assertThat(result.getUpdatedAt()).isEqualTo(fixedNow);
  }

  @Test
  void toEntities_shouldMapMultipleCardsToStagingCards() {
    List<JsonNode> cards = StagingCardMock.createMockJsonCardsList(2);

    List<StagingCard> results = mapper.toEntities(cards, fixedBatchId, fixedNow);

    assertThat(results).hasSize(2);

    assertThat(results.get(0).getId()).isEqualTo("card0");
    assertThat(results.get(0).getPayload()).contains("Test Card 0");
    assertThat(results.get(0).getBatchId()).isEqualTo(fixedBatchId);
    assertThat(results.get(0).getUpdatedAt()).isEqualTo(fixedNow);

    assertThat(results.get(1).getId()).isEqualTo("card1");
    assertThat(results.get(1).getPayload()).contains("Test Card 1");
    assertThat(results.get(1).getBatchId()).isEqualTo(fixedBatchId);
    assertThat(results.get(1).getUpdatedAt()).isEqualTo(fixedNow);
  }

  @Test
  void toEntities_shouldHandleEmptyList() {
    List<JsonNode> emptyList = StagingCardMock.createMockJsonCardsList(0);

    List<StagingCard> results = mapper.toEntities(emptyList, fixedBatchId, fixedNow);

    assertThat(results).isEmpty();
  }
}
