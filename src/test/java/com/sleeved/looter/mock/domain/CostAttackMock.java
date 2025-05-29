package com.sleeved.looter.mock.domain;

import com.sleeved.looter.domain.entity.atlas.Attack;
import com.sleeved.looter.domain.entity.atlas.CostAttack;
import com.sleeved.looter.domain.entity.atlas.Type;

public class CostAttackMock {

  public static CostAttack createMockCostAttack(Attack attack, Type type, Integer cost, Boolean isFree) {
    CostAttack costAttack = new CostAttack();
    costAttack.setAttack(attack);
    costAttack.setType(type);
    costAttack.setCost(cost);
    costAttack.setIsFree(isFree);
    return costAttack;
  }

  public static CostAttack createMockCostAttackSavedInDb(Integer id, Attack attack, Type type, Integer cost,
      Boolean isFree) {
    CostAttack costAttack = createMockCostAttack(attack, type, cost, isFree);
    costAttack.setId(id);
    return costAttack;
  }

  public static CostAttack createMockFreeCostAttack(Attack attack) {
    return createMockCostAttack(attack, null, 0, true);
  }

  public static CostAttack createMockFreeCostAttackSavedInDb(Integer id, Attack attack) {
    return createMockCostAttackSavedInDb(id, attack, null, 0, true);
  }
}