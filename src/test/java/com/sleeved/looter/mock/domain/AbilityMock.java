package com.sleeved.looter.mock.domain;

import com.sleeved.looter.domain.entity.atlas.Ability;

public class AbilityMock {
  public static Ability createMockAbility(String name, String text, String type) {
    Ability ability = new Ability();
    ability.setName(name);
    ability.setText(text);
    ability.setType(type);
    return ability;
  }

  public static Ability createMockAbilitySavedInDb(int id, String name, String text, String type) {
    Ability ability = new Ability();
    ability.setId(id);
    ability.setName(name);
    ability.setText(text);
    ability.setType(type);
    return ability;
  }
}
