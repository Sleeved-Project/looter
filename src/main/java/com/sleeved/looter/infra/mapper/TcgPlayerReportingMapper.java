package com.sleeved.looter.infra.mapper;

import org.springframework.stereotype.Component;

import com.sleeved.looter.infra.dto.TcgPlayerDTO;
import com.sleeved.looter.common.util.Constantes;
import com.sleeved.looter.common.util.DateUtil;
import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.entity.atlas.TcgPlayerReporting;

@Component
public class TcgPlayerReportingMapper {
  public TcgPlayerReporting toEntity(TcgPlayerDTO tcgPlayerDTO, Card card) {
    if (tcgPlayerDTO == null || card == null) {
      return null;
    }
    TcgPlayerReporting tcgPlayerReporting = new TcgPlayerReporting();
    tcgPlayerReporting.setUrl(tcgPlayerDTO.getUrl());
    tcgPlayerReporting
        .setUpdatedAt(DateUtil.parseDate(tcgPlayerDTO.getUpdatedAt(), Constantes.STAGING_CARD_DATE_FORMAT));
    tcgPlayerReporting.setCard(card);
    return tcgPlayerReporting;
  }
}
