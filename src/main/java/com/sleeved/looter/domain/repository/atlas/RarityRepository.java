package com.sleeved.looter.domain.repository.atlas;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.sleeved.looter.domain.entity.atlas.Rarity;

public interface RarityRepository extends JpaRepository<Rarity, Integer> {
  Optional<Rarity> findByLabel(String label);
}