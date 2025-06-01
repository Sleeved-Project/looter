package com.sleeved.looter.infra.dto;

import com.sleeved.looter.domain.entity.atlas.CardMarketPrice;
import com.sleeved.looter.domain.entity.atlas.TcgPlayerReporting;

import lombok.Data;

@Data
public class ReportingPriceEntitiesProcessedDTO {
  private TcgPlayerReporting tcgPlayerReporting;
  private CardMarketPrice cardMarketPrice;
}
