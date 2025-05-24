package com.sleeved.looter.infra.mapper;

import org.springframework.stereotype.Component;

import com.sleeved.looter.domain.entity.atlas.Legalities;
import com.sleeved.looter.infra.dto.LegalitiesDTO;

@Component
public class LegalitiesMapper {
  public Legalities toEntity(LegalitiesDTO legalitiesDTO) {
    Legalities legalities = new Legalities();
    if (legalitiesDTO == null) {
      legalities.setStandard(null);
      legalities.setExpanded(null);
      legalities.setUnlimited(null);
      return legalities;
    }
    legalities.setStandard(legalitiesDTO.getStandard());
    legalities.setExpanded(legalitiesDTO.getExpanded());
    legalities.setUnlimited(legalitiesDTO.getUnlimited());
    return legalities;
  }
}
