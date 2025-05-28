package com.sleeved.looter.mock.domain;

import com.sleeved.looter.domain.entity.atlas.Type;
import com.sleeved.looter.domain.entity.atlas.Weakness;

public class WeaknessMock {

  public static Weakness createMockWeakness(Type type, String value) {
    Weakness weakness = new Weakness();
    weakness.setType(type);
    weakness.setValue(value);
    return weakness;
  }

  public static Weakness createMockWeaknessSavedInDb(int id, Type type, String value) {
    Weakness weakness = createMockWeakness(type, value);
    weakness.setId(id);
    return weakness;
  }
}
