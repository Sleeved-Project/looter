package com.sleeved.looter.mock.domain;

import com.sleeved.looter.domain.entity.atlas.Legalities;

public class LegalitiesMock {
  public static Legalities createMockLegalities(String standard, String expanded, String unlimited) {
    Legalities legalities = new Legalities();
    legalities.setStandard(standard);
    legalities.setExpanded(expanded);
    legalities.setUnlimited(unlimited);
    return legalities;
  }

  public static Legalities createMockLegalitiesSavedInDb(int id, String standard, String expanded, String unlimited) {
    Legalities legalities = new Legalities();
    legalities.setId(id);
    legalities.setStandard(standard);
    legalities.setExpanded(expanded);
    legalities.setUnlimited(unlimited);
    return legalities;
  }
}