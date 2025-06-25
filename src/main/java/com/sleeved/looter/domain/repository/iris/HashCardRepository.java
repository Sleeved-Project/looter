package com.sleeved.looter.domain.repository.iris;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sleeved.looter.domain.entity.iris.HashCard;

public interface HashCardRepository extends JpaRepository<HashCard, String> {
}
