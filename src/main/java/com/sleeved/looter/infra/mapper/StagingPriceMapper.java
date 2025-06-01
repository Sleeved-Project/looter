package com.sleeved.looter.infra.mapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.sleeved.looter.domain.entity.staging.StagingPrice;

@Component
public class StagingPriceMapper {
  public StagingPrice toEntity(JsonNode cardPriceJson, Long batchId, LocalDateTime now) {
    StagingPrice entity = new StagingPrice();
    entity.setId(cardPriceJson.path("id").asText());
    entity.setPayload(cardPriceJson.toString());
    entity.setUpdatedAt(now);
    entity.setBatchId(batchId);
    return entity;
  }

  public List<StagingPrice> toEntities(List<JsonNode> cardPrices, Long batchId, LocalDateTime now) {
    return cardPrices.stream()
        .map(cardPriceJson -> toEntity(cardPriceJson, batchId, now))
        .collect(Collectors.toList());
  }
}
