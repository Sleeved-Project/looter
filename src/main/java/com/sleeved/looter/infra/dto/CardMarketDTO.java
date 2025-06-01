package com.sleeved.looter.infra.dto;

import lombok.Data;

@Data
public class CardMarketDTO {
  private String url;
  private String updatedAt;
  private CardMarketPriceDTO prices;
}
