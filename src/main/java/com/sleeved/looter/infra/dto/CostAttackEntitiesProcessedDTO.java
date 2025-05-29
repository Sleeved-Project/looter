package com.sleeved.looter.infra.dto;

import java.util.List;

import com.sleeved.looter.domain.entity.atlas.CostAttack;

import lombok.Data;

@Data
public class CostAttackEntitiesProcessedDTO {
  private List<CostAttack> costAttacks;
}
