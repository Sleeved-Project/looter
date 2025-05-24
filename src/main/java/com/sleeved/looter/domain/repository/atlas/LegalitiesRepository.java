package com.sleeved.looter.domain.repository.atlas;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sleeved.looter.domain.entity.atlas.Legalities;

public interface LegalitiesRepository extends JpaRepository<Legalities, Integer> {
  Optional<Legalities> findByStandardAndExpandedAndUnlimited(String standard, String expanded, String unlimited);;
}