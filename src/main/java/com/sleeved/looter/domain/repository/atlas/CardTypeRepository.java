package com.sleeved.looter.domain.repository.atlas;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.entity.atlas.CardType;
import com.sleeved.looter.domain.entity.atlas.Type;

@Repository
public interface CardTypeRepository extends JpaRepository<CardType, Integer> {
  Optional<CardType> findByTypeAndCard(Type type, Card card);
}