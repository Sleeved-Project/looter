package com.sleeved.looter.domain.repository.atlas;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.entity.atlas.CardResistance;
import com.sleeved.looter.domain.entity.atlas.Resistance;

@Repository
public interface CardResistanceRepository extends JpaRepository<CardResistance, Integer> {
  Optional<CardResistance> findByResistanceAndCard(Resistance resistance, Card card);
}