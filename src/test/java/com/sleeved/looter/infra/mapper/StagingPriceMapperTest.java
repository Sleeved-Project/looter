package com.sleeved.looter.infra.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.sleeved.looter.domain.entity.staging.StagingPrice;
import com.sleeved.looter.mock.domain.StagingPriceMock;

public class StagingPriceMapperTest {

  private StagingPriceMapper mapper;
  private LocalDateTime fixedNow;
  private Long fixedBatchId;

  @BeforeEach
  void setUp() {
    mapper = new StagingPriceMapper();
    fixedNow = LocalDateTime.of(2025, 5, 11, 14, 30);
    fixedBatchId = 123L;
  }

  @Test
  void toEntity_shouldMapCardPriceJsonToStagingPrice() {
    JsonNode cardPriceJson = StagingPriceMock.createMockJsonCardPrice("price1", "Test Price");

    StagingPrice result = mapper.toEntity(cardPriceJson, fixedBatchId, fixedNow);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo("price1");
    assertThat(result.getPayload()).contains("Test Price");
    assertThat(result.getBatchId()).isEqualTo(fixedBatchId);
    assertThat(result.getUpdatedAt()).isEqualTo(fixedNow);
  }

  @Test
  void toEntities_shouldMapMultiplePricesToStagingPrices() {
    List<JsonNode> cardPrices = StagingPriceMock.createMockJsonCardPricesList(2);

    List<StagingPrice> results = mapper.toEntities(cardPrices, fixedBatchId, fixedNow);

    assertThat(results).hasSize(2);

    assertThat(results.get(0).getId()).isEqualTo("price0");
    assertThat(results.get(0).getPayload()).contains("Test Price 0");
    assertThat(results.get(0).getBatchId()).isEqualTo(fixedBatchId);
    assertThat(results.get(0).getUpdatedAt()).isEqualTo(fixedNow);

    assertThat(results.get(1).getId()).isEqualTo("price1");
    assertThat(results.get(1).getPayload()).contains("Test Price 1");
    assertThat(results.get(1).getBatchId()).isEqualTo(fixedBatchId);
    assertThat(results.get(1).getUpdatedAt()).isEqualTo(fixedNow);
  }

  @Test
  void toEntities_shouldHandleEmptyList() {
    List<JsonNode> emptyList = StagingPriceMock.createMockJsonCardPricesList(0);

    List<StagingPrice> results = mapper.toEntities(emptyList, fixedBatchId, fixedNow);

    assertThat(results).isEmpty();
  }
}