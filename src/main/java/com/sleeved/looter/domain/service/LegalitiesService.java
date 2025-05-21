package com.sleeved.looter.domain.service;

import org.springframework.stereotype.Service;

import com.sleeved.looter.domain.entity.atlas.Legalities;
import com.sleeved.looter.domain.repository.atlas.LegalitiesRepository;

@Service
public class LegalitiesService {

  private final LegalitiesRepository legalitiesRepository;

  public LegalitiesService(LegalitiesRepository legalitiesRepository) {
    this.legalitiesRepository = legalitiesRepository;
  }

  public Legalities getOrCreate(Legalities legality) {
    return legalitiesRepository
        .findByStandardAndExpandedAndUnlimited(legality.getStandard(), legality.getExpanded(), legality.getUnlimited())
        .orElseGet(() -> legalitiesRepository.save(legality));
  }
}
