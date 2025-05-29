package com.sleeved.looter.domain.repository.atlas;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sleeved.looter.domain.entity.atlas.Attack;

public interface AttackRepository extends JpaRepository<Attack, Integer> {
  Optional<Attack> findByNameAndDamageAndConvertedEnergyCostAndText(String name, String damage,
      Integer convertedEnergyCost, String text);
}
