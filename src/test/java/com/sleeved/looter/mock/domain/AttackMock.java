package com.sleeved.looter.mock.domain;

import com.sleeved.looter.domain.entity.atlas.Attack;

public class AttackMock {
  public static Attack createMockAttack(String name, String damage, Integer convertedEnergyCost, String text) {
    Attack attack = new Attack();
    attack.setName(name);
    attack.setDamage(damage);
    attack.setConvertedEnergyCost(convertedEnergyCost);
    attack.setText(text);
    return attack;
  }

  public static Attack createMockAttackSavedInDb(int id, String name, String damage, Integer convertedEnergyCost,
      String text) {
    Attack attack = new Attack();
    attack.setId(id);
    attack.setName(name);
    attack.setDamage(damage);
    attack.setConvertedEnergyCost(convertedEnergyCost);
    attack.setText(text);
    return attack;
  }
}