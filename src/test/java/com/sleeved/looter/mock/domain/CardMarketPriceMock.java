package com.sleeved.looter.mock.domain;

import java.time.LocalDate;

import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.entity.atlas.CardMarketPrice;

public class CardMarketPriceMock {

  public static CardMarketPrice createMockCardMarketPrice(Long id, String url, LocalDate updatedAt, Card card,
      Double averageSellPrice, Double lowPrice, Double trendPrice,
      Double germanProLow, Double suggestedPrice, Double reverseHoloSell,
      Double reverseHoloLow, Double reverseHoloTrend, Double lowPriceExPlus,
      Double avg1, Double avg7, Double avg30,
      Double reverseHoloAvg1, Double reverseHoloAvg7, Double reverseHoloAvg30) {
    CardMarketPrice price = new CardMarketPrice();
    price.setId(id);
    price.setUrl(url);
    price.setUpdatedAt(updatedAt);
    price.setCard(card);
    price.setAverageSellPrice(averageSellPrice);
    price.setLowPrice(lowPrice);
    price.setTrendPrice(trendPrice);
    price.setGermanProLow(germanProLow);
    price.setSuggestedPrice(suggestedPrice);
    price.setReverseHoloSell(reverseHoloSell);
    price.setReverseHoloLow(reverseHoloLow);
    price.setReverseHoloTrend(reverseHoloTrend);
    price.setLowPriceExPlus(lowPriceExPlus);
    price.setAvg1(avg1);
    price.setAvg7(avg7);
    price.setAvg30(avg30);
    price.setReverseHoloAvg1(reverseHoloAvg1);
    price.setReverseHoloAvg7(reverseHoloAvg7);
    price.setReverseHoloAvg30(reverseHoloAvg30);
    return price;
  }

  public static CardMarketPrice createBasicMockCardMarketPrice(String url, LocalDate updatedAt, Card card) {
    return createMockCardMarketPrice(
        null, url, updatedAt, card,
        10.0, 8.0, 9.5, 9.0, 11.0,
        12.0, 10.5, 11.5, 9.0,
        10.2, 9.8, 9.5,
        11.2, 10.8, 10.5);
  }

  public static CardMarketPrice createMockCardMarketPriceSavedInDb(Long id, String url, LocalDate updatedAt, Card card,
      Double averageSellPrice, Double lowPrice, Double trendPrice,
      Double germanProLow, Double suggestedPrice, Double reverseHoloSell,
      Double reverseHoloLow, Double reverseHoloTrend, Double lowPriceExPlus,
      Double avg1, Double avg7, Double avg30,
      Double reverseHoloAvg1, Double reverseHoloAvg7, Double reverseHoloAvg30) {
    return createMockCardMarketPrice(
        id, url, updatedAt, card,
        averageSellPrice, lowPrice, trendPrice,
        germanProLow, suggestedPrice, reverseHoloSell,
        reverseHoloLow, reverseHoloTrend, lowPriceExPlus,
        avg1, avg7, avg30,
        reverseHoloAvg1, reverseHoloAvg7, reverseHoloAvg30);
  }
}