package com.sleeved.looter.mock.domain;

import com.sleeved.looter.domain.entity.atlas.Type;
import com.sleeved.looter.domain.entity.atlas.Resistance;

public class ResistanceMock {

  public static Resistance createMockResistance(Type type, String value) {
    Resistance resistance = new Resistance();
    resistance.setType(type);
    resistance.setValue(value);
    return resistance;
  }

  public static Resistance createMockResistanceSavedInDb(int id, Type type, String value) {
    Resistance resistance = createMockResistance(type, value);
    resistance.setId(id);
    return resistance;
  }
}
