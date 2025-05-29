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
    return attackRepository.findByNameAndDamageAndConvertedEnergyCostAndText(
        attack.getName(),
        attack.getDamage(),
        attack.getConvertedEnergyCost(),
        attack.getText())
        .orElseGet(() -> attackRepository.save(attack));
  }

  public Attack getByNameAndDamageAndConvertedEnegyCostAndText(Attack attack) {
    return attackRepository.findByNameAndDamageAndConvertedEnergyCostAndText(
        attack.getName(),
        attack.getDamage(),
        attack.getConvertedEnergyCost(), attack.getText())
        .orElseThrow(() -> new RuntimeException(
            "Attack not found for name: " + attack.getName() + ", damage: " + attack.getDamage()
                + ", converted energy cost: " + attack.getConvertedEnergyCost() + ", text: " + attack.getText()));
  }
}
