package com.sleeved.looter.domain.repository.atlas;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sleeved.looter.domain.entity.atlas.Type;
import com.sleeved.looter.domain.entity.atlas.Weakness;

public interface WeaknessRepository extends JpaRepository<Weakness, Integer> {
  Optional<Weakness> findByTypeAndValue(Type type, String value);
}
