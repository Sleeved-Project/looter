package com.sleeved.looter.domain.repository.atlas;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.entity.atlas.CardWeakness;
import com.sleeved.looter.domain.entity.atlas.Weakness;

@Repository
public interface CardWeaknessRepository extends JpaRepository<CardWeakness, Integer> {
  Optional<CardWeakness> findByWeaknessAndCard(Weakness weakness, Card card);
}