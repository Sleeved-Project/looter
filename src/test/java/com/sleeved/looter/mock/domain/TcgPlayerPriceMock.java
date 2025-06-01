package com.sleeved.looter.mock.domain;

import com.sleeved.looter.domain.entity.atlas.TcgPlayerPrice;
import com.sleeved.looter.domain.entity.atlas.TcgPlayerReporting;

public class TcgPlayerPriceMock {

  public static TcgPlayerPrice createMockTcgPlayerPrice(Long id, String type, Double lowPrice, Double midPrice,
      Double highPrice, Double marketPrice, Double directLowPrice,
      TcgPlayerReporting tcgPlayerReporting) {
    TcgPlayerPrice price = new TcgPlayerPrice();
    price.setId(id);
    price.setType(type);
    price.setLow(lowPrice);
    price.setMid(midPrice);
    price.setHigh(highPrice);
    price.setMarket(marketPrice);
    price.setDirectLow(directLowPrice);
    price.setTcgPlayerReporting(tcgPlayerReporting);
    return price;
  }

  public static TcgPlayerPrice createMockTcgPlayerPriceSavedInDb(Long id, String type, Double lowPrice, Double midPrice,
      Double highPrice, Double marketPrice, Double directLowPrice,
      TcgPlayerReporting tcgPlayerReporting) {
    return createMockTcgPlayerPrice(id, type, lowPrice, midPrice, highPrice, marketPrice, directLowPrice,
        tcgPlayerReporting);
  }
}