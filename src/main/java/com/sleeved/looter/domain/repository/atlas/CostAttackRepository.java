package com.sleeved.looter.domain.repository.atlas;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sleeved.looter.domain.entity.atlas.Attack;
import com.sleeved.looter.domain.entity.atlas.CostAttack;
import com.sleeved.looter.domain.entity.atlas.Type;

public interface CostAttackRepository extends JpaRepository<CostAttack, Integer> {
  Optional<CostAttack> findByAttackAndTypeAndCost(Attack attack, Type type, Integer cost);

  Optional<CostAttack> findByAttackAndIsFree(Attack attack, Boolean isFree);
}
