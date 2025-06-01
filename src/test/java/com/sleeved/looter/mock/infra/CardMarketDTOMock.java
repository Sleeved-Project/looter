package com.sleeved.looter.mock.infra;

import com.sleeved.looter.infra.dto.CardMarketDTO;

public class CardMarketDTOMock {

  public static CardMarketDTO createMockCardMarketDTO(String url, String updatedAt) {
    CardMarketDTO dto = new CardMarketDTO();
    dto.setUrl(url);
    dto.setUpdatedAt(updatedAt);
    return dto;
  }
}