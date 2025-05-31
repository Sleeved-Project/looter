package com.sleeved.looter.domain.service;

import org.springframework.stereotype.Service;

import com.sleeved.looter.domain.entity.atlas.Ability;
import com.sleeved.looter.domain.repository.atlas.AbilityRepository;

@Service
public class AbilityService {

  private final AbilityRepository abilityRepository;

  public AbilityService(AbilityRepository abilityRepository) {
    this.abilityRepository = abilityRepository;
  }

  public Ability getOrCreate(Ability ability) {
    return abilityRepository.findByNameAndTypeAndText(ability.getName(), ability.getType(), ability.getText())
        .orElseGet(() -> abilityRepository.save(ability));
  }

  public Ability getByNameAndTypeAndText(Ability ability) {
    return abilityRepository.findByNameAndTypeAndText(ability.getName(), ability.getType(), ability.getText())
        .orElseThrow(() -> new RuntimeException(
            "Ability not found for name: " + ability.getName() + ", type: " + ability.getType()
                + ", text: " + ability.getText()));
  }
}
