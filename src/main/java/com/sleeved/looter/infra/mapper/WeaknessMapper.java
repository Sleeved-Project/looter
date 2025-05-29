package com.sleeved.looter.infra.mapper;

import org.springframework.stereotype.Component;

import com.sleeved.looter.domain.entity.atlas.Type;
import com.sleeved.looter.domain.entity.atlas.Weakness;
import com.sleeved.looter.infra.dto.WeaknessDTO;

@Component
public class WeaknessMapper {
  public Weakness toEntity(WeaknessDTO weaknessDTO, Type weaknessType) {
    Weakness weakness = new Weakness();
    weakness.setValue(weaknessDTO.getValue());
    weakness.setType(weaknessType);
    return weakness;
  }
}
