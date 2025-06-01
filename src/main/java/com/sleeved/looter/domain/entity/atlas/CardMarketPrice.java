package com.sleeved.looter.domain.entity.atlas;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "Card_Market_Price")
@Data
public class CardMarketPrice {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(columnDefinition = "mediumtext")
  private String url;

  @Column(name = "average_sell_price", precision = 10, scale = 0)
  private Double averageSellPrice;

  @Column(name = "low_price", precision = 10, scale = 0)
  private Double lowPrice;

  @Column(name = "trend_price", precision = 10, scale = 0)
  private Double trendPrice;

  @Column(name = "german_pro_low", precision = 10, scale = 0)
  private Double germanProLow;

  @Column(name = "suggested_price", precision = 10, scale = 0)
  private Double suggestedPrice;

  @Column(name = "reverse_holo_sell", precision = 10, scale = 0)
  private Double reverseHoloSell;

  @Column(name = "reverse_holo_low", precision = 10, scale = 0)
  private Double reverseHoloLow;

  @Column(name = "reverse_holo_trend", precision = 10, scale = 0)
  private Double reverseHoloTrend;

  @Column(name = "low_price_ex_plus", precision = 10, scale = 0)
  private Double lowPriceExPlus;

  @Column(name = "avg_1", precision = 10, scale = 0)
  private Double avg1;

  @Column(name = "avg_7", precision = 10, scale = 0)
  private Double avg7;

  @Column(name = "avg_30", precision = 10, scale = 0)
  private Double avg30;

  @Column(name = "reverse_holo_avg_1", precision = 10, scale = 0)
  private Double reverseHoloAvg1;

  @Column(name = "reverse_holo_avg_7", precision = 10, scale = 0)
  private Double reverseHoloAvg7;

  @Column(name = "reverse_holo_avg_30", precision = 10, scale = 0)
  private Double reverseHoloAvg30;

  @Column(name = "updated_at", nullable = false)
  private LocalDate updatedAt;

  @ManyToOne
  @JoinColumn(name = "card_id", nullable = false)
  private Card card;
}
