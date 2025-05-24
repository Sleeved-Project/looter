package com.sleeved.looter.domain.repository.atlas;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sleeved.looter.domain.entity.atlas.Subtype;

public interface SubtypeRepository extends JpaRepository<Subtype, Integer> {
  Optional<Subtype> findByLabel(String label);
}
