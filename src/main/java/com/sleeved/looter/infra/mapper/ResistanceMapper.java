package com.sleeved.looter.infra.mapper;

import org.springframework.stereotype.Component;

import com.sleeved.looter.domain.entity.atlas.Type;
import com.sleeved.looter.domain.entity.atlas.Resistance;
import com.sleeved.looter.infra.dto.ResistanceDTO;

@Component
public class ResistanceMapper {
  public Resistance toEntity(ResistanceDTO resistanceDTO, Type resistanceType) {
    Resistance resistance = new Resistance();
    resistance.setValue(resistanceDTO.getValue());
    resistance.setType(resistanceType);
    return resistance;
  }
}
