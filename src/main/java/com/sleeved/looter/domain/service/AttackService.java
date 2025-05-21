package com.sleeved.looter.domain.service;

import org.springframework.stereotype.Service;

import com.sleeved.looter.domain.entity.atlas.Attack;
import com.sleeved.looter.domain.repository.atlas.AttackRepository;

@Service
public class AttackService {

  private final AttackRepository attackRepository;

  public AttackService(AttackRepository attackRepository) {
    this.attackRepository = attackRepository;
  }

  public Attack getOrCreate(Attack attack) {
    return attackRepository.findByNameAndDamageAndConvertedEnergyCost(
        attack.getName(),
        attack.getDamage(),
        attack.getConvertedEnergyCost())
        .orElseGet(() -> attackRepository.save(attack));
  }
}
