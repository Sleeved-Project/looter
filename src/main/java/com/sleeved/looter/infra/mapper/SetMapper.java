package com.sleeved.looter.infra.mapper;

import org.springframework.stereotype.Component;

import com.sleeved.looter.common.util.Constantes;
import com.sleeved.looter.common.util.DateUtil;
import com.sleeved.looter.domain.entity.atlas.Legalities;
import com.sleeved.looter.domain.entity.atlas.Set;
import com.sleeved.looter.infra.dto.SetDTO;

@Component
public class SetMapper {
  public Set toEntity(SetDTO setDTO, Legalities setLegatities) {
    Set set = new Set();
    set.setId(setDTO.getId());
    set.setName(setDTO.getName());
    set.setSeries(setDTO.getSeries());
    set.setPrintedTotal(setDTO.getPrintedTotal());
    set.setTotal(setDTO.getTotal());
    set.setPtcgoCode(setDTO.getPtcgoCode());
    set.setReleaseDate(DateUtil.parseDate(setDTO.getReleaseDate(), Constantes.STAGING_CARD_DATE_FORMAT));
    set.setUpdatedAt(DateUtil.parseDateTime(setDTO.getUpdatedAt(), Constantes.STAGING_CARD_DATE_TIME_FORMAT));
    set.setImageSymbol(setDTO.getImages().getSymbol());
    set.setImageLogo(setDTO.getImages().getLogo());
    set.setLegalities(setLegatities);
    return set;
  }
}
