package com.sleeved.looter.domain.repository.atlas;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sleeved.looter.domain.entity.atlas.Set;

public interface SetRepository extends JpaRepository<Set, String> {
}
