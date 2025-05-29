package com.sleeved.looter.domain.service;

import org.springframework.stereotype.Service;

import com.sleeved.looter.domain.entity.atlas.Resistance;
import com.sleeved.looter.domain.repository.atlas.ResistanceRepository;

@Service
public class ResistanceService {

  private final ResistanceRepository resistanceRepository;

  public ResistanceService(ResistanceRepository resistanceRepository) {
    this.resistanceRepository = resistanceRepository;
  }

  public Resistance getOrCreate(Resistance resistance) {
    return resistanceRepository.findByTypeAndValue(resistance.getType(), resistance.getValue())
        .orElseGet(() -> resistanceRepository.save(resistance));
  }
}