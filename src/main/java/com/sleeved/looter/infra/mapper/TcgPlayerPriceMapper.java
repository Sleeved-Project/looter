package com.sleeved.looter.infra.mapper;

import org.springframework.stereotype.Component;

import com.sleeved.looter.infra.dto.TcgPlayerPriceDTO;
import com.sleeved.looter.domain.entity.atlas.TcgPlayerPrice;
import com.sleeved.looter.domain.entity.atlas.TcgPlayerReporting;

@Component
public class TcgPlayerPriceMapper {
  public TcgPlayerPrice toEntity(TcgPlayerPriceDTO tcgPlayerPricesDTO, String tcgPlayerPricesType,
      TcgPlayerReporting tcgPlayerReporting) {
    TcgPlayerPrice tcgPlayerPrice = new TcgPlayerPrice();
    if (tcgPlayerPricesDTO == null || tcgPlayerReporting == null) {
      return null;
    }
    tcgPlayerPrice.setTcgPlayerReporting(tcgPlayerReporting);
    tcgPlayerPrice.setType(tcgPlayerPricesType);
    tcgPlayerPrice.setLow(tcgPlayerPricesDTO.getLow());
    tcgPlayerPrice.setMid(tcgPlayerPricesDTO.getMid());
    tcgPlayerPrice.setHigh(tcgPlayerPricesDTO.getHigh());
    tcgPlayerPrice.setMarket(tcgPlayerPricesDTO.getMarket());
    tcgPlayerPrice.setDirectLow(tcgPlayerPricesDTO.getDirectLow());
    return tcgPlayerPrice;
  }
}
