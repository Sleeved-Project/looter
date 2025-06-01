package com.sleeved.looter.mock.infra;

import com.sleeved.looter.infra.dto.TcgPlayerPriceDTO;

public class TcgPlayerPriceDTOMock {

  public static TcgPlayerPriceDTO createMockTcgPlayerPriceDTO(Double low, Double mid, Double high,
      Double market, Double directLow) {
    TcgPlayerPriceDTO dto = new TcgPlayerPriceDTO();
    dto.setLow(low);
    dto.setMid(mid);
    dto.setHigh(high);
    dto.setMarket(market);
    dto.setDirectLow(directLow);
    return dto;
  }
}