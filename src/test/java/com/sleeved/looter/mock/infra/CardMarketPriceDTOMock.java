package com.sleeved.looter.mock.infra;

import com.sleeved.looter.infra.dto.CardMarketPriceDTO;

public class CardMarketPriceDTOMock {

  public static CardMarketPriceDTO createMockCardMarketPriceDTO(
      Double averageSellPrice, Double avg1, Double avg7, Double avg30, Double germanProLow,
      Double lowPrice, Double lowPriceExPlus, Double reverseHoloAvg1, Double reverseHoloAvg7, Double reverseHoloAvg30,
      Double reverseHoloLow, Double reverseHoloSell, Double reverseHoloTrend, Double suggestedPrice,
      Double trendPrice) {

    CardMarketPriceDTO dto = new CardMarketPriceDTO();
    dto.setAverageSellPrice(averageSellPrice);
    dto.setAvg1(avg1);
    dto.setAvg7(avg7);
    dto.setAvg30(avg30);
    dto.setGermanProLow(germanProLow);
    dto.setLowPrice(lowPrice);
    dto.setLowPriceExPlus(lowPriceExPlus);
    dto.setReverseHoloAvg1(reverseHoloAvg1);
    dto.setReverseHoloAvg7(reverseHoloAvg7);
    dto.setReverseHoloAvg30(reverseHoloAvg30);
    dto.setReverseHoloLow(reverseHoloLow);
    dto.setReverseHoloSell(reverseHoloSell);
    dto.setReverseHoloTrend(reverseHoloTrend);
    dto.setSuggestedPrice(suggestedPrice);
    dto.setTrendPrice(trendPrice);
    return dto;
  }
}