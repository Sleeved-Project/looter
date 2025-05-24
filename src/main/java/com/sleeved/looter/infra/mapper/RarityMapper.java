package com.sleeved.looter.infra.mapper;

import org.springframework.stereotype.Component;

import com.sleeved.looter.domain.entity.atlas.Rarity;

@Component
public class RarityMapper {
  public Rarity toEntity(String rarityLabel) {
    Rarity rarity = new Rarity();
    if (rarityLabel != null && !rarityLabel.isBlank()) {
      rarity.setLabel(rarityLabel.trim());
    } else {
      rarity.setLabel("UNKNOWN");
    }
    return rarity;
  }
}
