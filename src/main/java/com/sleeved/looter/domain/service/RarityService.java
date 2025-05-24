package com.sleeved.looter.domain.service;

import org.springframework.stereotype.Service;

import com.sleeved.looter.domain.entity.atlas.Rarity;
import com.sleeved.looter.domain.repository.atlas.RarityRepository;

@Service
public class RarityService {

  private final RarityRepository rarityRepository;

  public RarityService(RarityRepository rarityRepository) {
    this.rarityRepository = rarityRepository;
  }

  public Rarity getOrCreate(Rarity rarity) {
    return rarityRepository.findByLabel(rarity.getLabel())
        .orElseGet(() -> rarityRepository.save(rarity));
  }
}
