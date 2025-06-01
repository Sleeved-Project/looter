package com.sleeved.looter.infra.dto;

import lombok.Data;

@Data
public class CardMarketPriceDTO {
  private Double avg1;
  private Double avg7;
  private Double avg30;
  private Double lowPrice;
  private Double trendPrice;
  private Double germanProLow;
  private Double lowPriceExPlus;
  private Double reverseHoloLow;
  private Double suggestedPrice;
  private Double reverseHoloAvg1;
  private Double reverseHoloAvg7;
  private Double reverseHoloSell;
  private Double averageSellPrice;
  private Double reverseHoloAvg30;
  private Double reverseHoloTrend;
}
