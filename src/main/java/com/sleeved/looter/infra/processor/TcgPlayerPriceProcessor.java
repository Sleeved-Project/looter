package com.sleeved.looter.infra.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.sleeved.looter.domain.entity.atlas.TcgPlayerPrice;
import com.sleeved.looter.domain.entity.atlas.TcgPlayerReporting;
import com.sleeved.looter.infra.dto.TcgPlayerPriceDTO;
import com.sleeved.looter.infra.mapper.TcgPlayerPriceMapper;

@Service
public class TcgPlayerPriceProcessor {

  private final TcgPlayerPriceMapper tcgPlayerPriceMapper;

  public TcgPlayerPriceProcessor(TcgPlayerPriceMapper tcgPlayerPriceMapper) {
    this.tcgPlayerPriceMapper = tcgPlayerPriceMapper;
  }

  public List<TcgPlayerPrice> processFromDTOs(Map<String, TcgPlayerPriceDTO> tcgPlayerPricesDTO,
      TcgPlayerReporting tcgPlayerReporting) {
    List<TcgPlayerPrice> tcgPlayerPrices = new ArrayList<>();

    if (tcgPlayerPricesDTO == null) {
      return tcgPlayerPrices;
    }

    for (Map.Entry<String, TcgPlayerPriceDTO> entry : tcgPlayerPricesDTO.entrySet()) {
      String type = entry.getKey();
      TcgPlayerPriceDTO tcgPlayerPriceDTO = entry.getValue();
      TcgPlayerPrice tcgPlayerPrice = tcgPlayerPriceMapper.toEntity(tcgPlayerPriceDTO, type, tcgPlayerReporting);
      if (tcgPlayerPrice == null) {
        continue;
      }
      tcgPlayerPrices.add(tcgPlayerPrice);
    }

    return tcgPlayerPrices;
  }
}