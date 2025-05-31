package com.sleeved.looter.domain.repository.atlas;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.entity.atlas.CardSubtype;
import com.sleeved.looter.domain.entity.atlas.Subtype;

@Repository
public interface CardSubtypeRepository extends JpaRepository<CardSubtype, Integer> {
  Optional<CardSubtype> findBySubtypeAndCard(Subtype subtype, Card card);
}