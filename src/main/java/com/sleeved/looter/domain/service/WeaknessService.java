package com.sleeved.looter.domain.service;

import org.springframework.stereotype.Service;

import com.sleeved.looter.domain.entity.atlas.Weakness;
import com.sleeved.looter.domain.repository.atlas.WeaknessRepository;

@Service
public class WeaknessService {

  private final WeaknessRepository weaknessRepository;

  public WeaknessService(WeaknessRepository weaknessRepository) {
    this.weaknessRepository = weaknessRepository;
  }

  public Weakness getOrCreate(Weakness weakness) {
    return weaknessRepository.findByTypeAndValue(weakness.getType(), weakness.getValue())
        .orElseGet(() -> weaknessRepository.save(weakness));
  }
}