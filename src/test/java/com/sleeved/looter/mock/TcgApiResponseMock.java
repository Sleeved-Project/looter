package com.sleeved.looter.mock;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class TcgApiResponseMock {
  private static final ObjectMapper objectMapper = new ObjectMapper();

  public static JsonNode createMockCardPage(int numberOfCards) {
    ObjectNode rootNode = objectMapper.createObjectNode();
    ArrayNode dataArray = objectMapper.createArrayNode();

    for (int i = 0; i < numberOfCards; i++) {
      ObjectNode card = objectMapper.createObjectNode();
      card.put("id", i);
      card.put("name", "Card " + i);
      dataArray.add(card);
    }

    rootNode.set("data", dataArray);
    rootNode.put("pageSize", numberOfCards);
    rootNode.put("totalCount", 30); // Total fictif

    return rootNode;
  }

}
