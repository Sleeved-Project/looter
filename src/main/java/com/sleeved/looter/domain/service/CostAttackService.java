package com.sleeved.looter.domain.service;

import org.springframework.stereotype.Service;

import com.sleeved.looter.domain.entity.atlas.CostAttack;
import com.sleeved.looter.domain.repository.atlas.CostAttackRepository;

@Service
public class CostAttackService {

  private final CostAttackRepository costAttackRepository;

  public CostAttackService(CostAttackRepository costAttackRepository) {
    this.costAttackRepository = costAttackRepository;
  }

  public CostAttack getOrCreate(CostAttack costAttack) {
    if (costAttack.getIsFree()) {
      return costAttackRepository
          .findByAttackAndIsFree(costAttack.getAttack(), true)
          .orElseGet(() -> costAttackRepository.save(costAttack));
    } else {
      return costAttackRepository
          .findByAttackAndTypeAndCost(costAttack.getAttack(), costAttack.getType(), costAttack.getCost())
          .orElseGet(() -> costAttackRepository.save(costAttack));
    }
  }
}