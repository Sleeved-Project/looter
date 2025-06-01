package com.sleeved.looter.infra.dto;

import java.util.Map;

import lombok.Data;

@Data
public class TcgPlayerDTO {
  private String url;
  private String updatedAt;
  private Map<String, TcgPlayerPriceDTO> prices;
}
