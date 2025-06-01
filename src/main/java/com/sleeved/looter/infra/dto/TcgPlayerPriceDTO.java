package com.sleeved.looter.infra.dto;

import lombok.Data;

@Data
public class TcgPlayerPriceDTO {
  private Double low;
  private Double mid;
  private Double high;
  private Double market;
  private Double directLow;
}
