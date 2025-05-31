package com.sleeved.looter.domain.repository.atlas;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sleeved.looter.domain.entity.atlas.Ability;
import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.entity.atlas.CardAbility;

public interface CardAbilityRepository extends JpaRepository<CardAbility, Integer> {
  Optional<CardAbility> findByAbilityAndCard(Ability ability, Card card);
}
