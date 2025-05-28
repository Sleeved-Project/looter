package com.sleeved.looter.infra.dto;

import java.util.List;

import com.sleeved.looter.domain.entity.atlas.Resistance;
import com.sleeved.looter.domain.entity.atlas.Set;
import com.sleeved.looter.domain.entity.atlas.Weakness;

import lombok.Data;

@Data
public class SetsWeaknessResistanceCardEntitiesProcessedDTO {
  private Set set;
  private List<Weakness> weaknesses;
  private List<Resistance> resistances;
}
