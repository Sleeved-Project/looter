package com.sleeved.looter.mock.domain;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sleeved.looter.domain.entity.staging.StagingPrice;

public class StagingPriceMock {
  private static final ObjectMapper mapper = new ObjectMapper();

  public static JsonNode createMockJsonCardPrice(String id, String name) {
    ObjectNode node = mapper.createObjectNode();
    node.put("id", id);
    node.put("name", name);

    // Ajout des informations de prix
    ObjectNode tcgplayer = mapper.createObjectNode();
    ObjectNode prices = mapper.createObjectNode();
    ObjectNode normal = mapper.createObjectNode();
    normal.put("low", 1.99);
    normal.put("mid", 2.99);
    normal.put("high", 3.99);
    normal.put("market", 2.49);
    prices.set("normal", normal);
    tcgplayer.set("prices", prices);
    node.set("tcgplayer", tcgplayer);

    return node;
  }

  public static List<JsonNode> createMockJsonCardPricesList(int count) {
    List<JsonNode> result = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      result.add(createMockJsonCardPrice("price" + i, "Test Price " + i));
    }
    return result;
  }

  public static StagingPrice createMockStagingPrice(String id, Long batchId) {
    StagingPrice price = new StagingPrice();
    price.setId(id);
    price.setPayload("{\"id\":\"" + id + "\",\"name\":\"Test Price\"}");
    price.setBatchId(batchId);
    return price;
  }

  public static List<StagingPrice> createMockStagingPricesList(int count, Long batchId) {
    List<StagingPrice> result = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      result.add(createMockStagingPrice("price" + i, batchId));
    }
    return result;
  }
}