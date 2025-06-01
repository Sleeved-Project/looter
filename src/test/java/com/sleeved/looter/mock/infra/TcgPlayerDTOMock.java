package com.sleeved.looter.mock.infra;

import java.util.Map;

import com.sleeved.looter.infra.dto.TcgPlayerDTO;
import com.sleeved.looter.infra.dto.TcgPlayerPriceDTO;

public class TcgPlayerDTOMock {

  public static TcgPlayerDTO createMockTcgPlayerDTO(String url, String updatedAt) {
    TcgPlayerDTO dto = new TcgPlayerDTO();
    dto.setUrl(url);
    dto.setUpdatedAt(updatedAt);
    return dto;
  }

  public static TcgPlayerDTO createMockTcgPlayerDTOWithPrices(String url, String updatedAt,
      Map<String, TcgPlayerPriceDTO> prices) {
    TcgPlayerDTO dto = createMockTcgPlayerDTO(url, updatedAt);
    dto.setPrices(prices);
    return dto;
  }
}