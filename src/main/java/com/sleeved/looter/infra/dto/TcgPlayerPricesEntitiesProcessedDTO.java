package com.sleeved.looter.infra.dto;

import java.util.List;

import com.sleeved.looter.domain.entity.atlas.TcgPlayerPrice;

import lombok.Data;

@Data
public class TcgPlayerPricesEntitiesProcessedDTO {
  private List<TcgPlayerPrice> tcgPlayerPrices;
}
