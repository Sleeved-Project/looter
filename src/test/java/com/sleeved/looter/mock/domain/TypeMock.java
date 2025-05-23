package com.sleeved.looter.mock.domain;

import com.sleeved.looter.domain.entity.atlas.Type;

public class TypeMock {

  public static Type createMockType(String label) {
    Type type = new Type();
    type.setLabel(label);
    return type;
  }

  public static Type createMockTypeSavedInDb(int id, String label) {
    Type type = new Type();
    type.setId(id);
    type.setLabel(label);
    return type;
  }
}