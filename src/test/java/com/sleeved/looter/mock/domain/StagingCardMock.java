package com.sleeved.looter.mock.domain;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sleeved.looter.domain.entity.staging.StagingCard;

public class StagingCardMock {
  private static final ObjectMapper objectMapper = new ObjectMapper();

  public static List<JsonNode> createMockJsonCardsList(int count) {
    List<JsonNode> cards = new ArrayList<>();

    for (int i = 0; i < count; i++) {
      ObjectNode card = objectMapper.createObjectNode();
      card.put("id", "card" + i);
      card.put("name", "Test Card " + i);
      cards.add(card);
    }

    return cards;
  }

  public static JsonNode createMockJsonCard(String id, String name) {
    ObjectNode card = objectMapper.createObjectNode();
    card.put("id", id);
    card.put("name", name);
    return card;
  }

  public static StagingCard createMockStagingCard(String id, String name) {
    StagingCard card = new StagingCard();
    card.setId(id);
    card.setPayload(name);
    return card;
  }

  public static List<StagingCard> createMockStagingCardsList(int count) {
    List<StagingCard> cards = new ArrayList<>();

    for (int i = 0; i < count; i++) {
      String id = "card-" + i;
      String payload = "{\"id\":\"" + id + "\",\"name\":\"Test Card " + i + "\"}";
      StagingCard card = createMockStagingCard(id, payload);
      cards.add(card);
    }

    return cards;
  }

}
