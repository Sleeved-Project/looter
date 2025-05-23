package com.sleeved.looter.infra.mapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.sleeved.looter.domain.entity.staging.StagingCard;

@Component
public class StagingCardMapper {
  public StagingCard toEntity(JsonNode cardJson, Long batchId, LocalDateTime now) {
    StagingCard entity = new StagingCard();
    entity.setId(cardJson.path("id").asText());
    entity.setPayload(cardJson.toString());
    entity.setUpdatedAt(now);
    entity.setBatchId(batchId);
    return entity;
  }

  public List<StagingCard> toEntities(List<JsonNode> cards, Long batchId, LocalDateTime now) {
    return cards.stream()
        .map(cardJson -> toEntity(cardJson, batchId, now))
        .collect(Collectors.toList());
  }
}
