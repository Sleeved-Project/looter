package com.sleeved.looter.domain.service;

import org.springframework.stereotype.Service;

import com.sleeved.looter.domain.entity.atlas.Type;
import com.sleeved.looter.domain.repository.atlas.TypeRepository;

@Service
public class TypeService {

  private final TypeRepository typeRepository;

  public TypeService(TypeRepository typeRepository) {
    this.typeRepository = typeRepository;
  }

  public Type getOrCreate(Type type) {
    return typeRepository.findByLabel(type.getLabel())
        .orElseGet(() -> typeRepository.save(type));
  }
}