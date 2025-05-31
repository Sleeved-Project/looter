package com.sleeved.looter.domain.repository.atlas;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sleeved.looter.domain.entity.atlas.Attack;
import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.entity.atlas.CardAttack;

@Repository
public interface CardAttackRepository extends JpaRepository<CardAttack, Integer> {
  Optional<CardAttack> findByAttackAndCard(Attack attack, Card card);
}