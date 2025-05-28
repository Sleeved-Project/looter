package com.sleeved.looter.domain.repository.atlas;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sleeved.looter.domain.entity.atlas.Resistance;
import com.sleeved.looter.domain.entity.atlas.Type;

public interface ResistanceRepository extends JpaRepository<Resistance, Integer> {
  Optional<Resistance> findByTypeAndValue(Type type, String value);
}
