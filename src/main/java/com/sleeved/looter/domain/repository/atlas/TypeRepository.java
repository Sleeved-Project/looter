package com.sleeved.looter.domain.repository.atlas;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sleeved.looter.domain.entity.atlas.Type;

public interface TypeRepository extends JpaRepository<Type, Integer> {
  Optional<Type> findByLabel(String label);
}