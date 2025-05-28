package com.sleeved.looter.domain.service;

import org.springframework.stereotype.Service;

import com.sleeved.looter.domain.entity.atlas.Set;
import com.sleeved.looter.domain.repository.atlas.SetRepository;

@Service
public class SetService {

  private final SetRepository setRepository;

  public SetService(SetRepository setRepository) {
    this.setRepository = setRepository;
  }

  public Set getOrCreate(Set set) {
    return setRepository.findById(set.getId())
        .orElseGet(() -> setRepository.save(set));
  }
}
