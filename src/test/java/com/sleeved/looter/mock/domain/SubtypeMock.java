package com.sleeved.looter.mock.domain;

import com.sleeved.looter.domain.entity.atlas.Subtype;

public class SubtypeMock {
  public static Subtype createMockSubtype(String label) {
    Subtype subtype = new Subtype();
    subtype.setLabel(label);
    return subtype;
  }

  public static Subtype createMockSubtypeSavedInDb(int id, String label) {
    Subtype subtype = new Subtype();
    subtype.setId(id);
    subtype.setLabel(label);
    return subtype;
  }

}
