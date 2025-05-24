package com.sleeved.looter.domain.service;

import org.springframework.stereotype.Service;

import com.sleeved.looter.domain.entity.atlas.Subtype;
import com.sleeved.looter.domain.repository.atlas.SubtypeRepository;

@Service
public class SubtypeService {

  private final SubtypeRepository subtypeRepository;

  public SubtypeService(SubtypeRepository subtypeRepository) {
    this.subtypeRepository = subtypeRepository;
  }

  public Subtype getOrCreate(Subtype subtype) {
    return subtypeRepository.findByLabel(subtype.getLabel())
        .orElseGet(() -> subtypeRepository.save(subtype));
  }
}
