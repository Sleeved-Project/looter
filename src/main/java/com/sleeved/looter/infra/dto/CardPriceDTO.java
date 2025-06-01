package com.sleeved.looter.infra.dto;

import lombok.Data;

@Data
public class CardPriceDTO {
  private String id;
  private String name;
  private TcgPlayerDTO tcgplayer;
  private CardMarketDTO cardmarket;
}
