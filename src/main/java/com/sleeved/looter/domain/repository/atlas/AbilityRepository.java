package com.sleeved.looter.domain.repository.atlas;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sleeved.looter.domain.entity.atlas.Ability;

public interface AbilityRepository extends JpaRepository<Ability, Integer> {
  Optional<Ability> findByNameAndTypeAndText(String name, String type, String text);
}
