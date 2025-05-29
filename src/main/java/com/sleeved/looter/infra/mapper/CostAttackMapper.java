package com.sleeved.looter.infra.mapper;

import org.springframework.stereotype.Component;

import com.sleeved.looter.domain.entity.atlas.Attack;
import com.sleeved.looter.domain.entity.atlas.CostAttack;
import com.sleeved.looter.domain.entity.atlas.Type;

@Component
public class CostAttackMapper {
  public CostAttack toEntity(Attack attack, Type type, Integer cost) {
    CostAttack costAttack = new CostAttack();
    costAttack.setAttack(attack);
    costAttack.setType(type);
    costAttack.setCost(cost);
    costAttack.setIsFree(false);
    return costAttack;
  }

  public CostAttack toFreeAttackEntity(Attack attack) {
    CostAttack costAttack = new CostAttack();
    costAttack.setAttack(attack);
    costAttack.setType(null);
    costAttack.setCost(0);
    costAttack.setIsFree(true);
    return costAttack;
  }

}
