package com.sleeved.looter.mock.domain;

import com.sleeved.looter.domain.entity.atlas.Rarity;

public class RarityMock {
  public static Rarity createMockRarity(String label) {
    Rarity rarity = new Rarity();
    rarity.setLabel(label);
    return rarity;
  }

  public static Rarity createMockRaritySavedInDb(int id, String label) {
    Rarity rarity = new Rarity();
    rarity.setId(id);
    rarity.setLabel(label);
    return rarity;
  }
}