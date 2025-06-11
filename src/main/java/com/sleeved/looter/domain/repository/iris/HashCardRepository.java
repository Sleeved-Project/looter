package com.sleeved.looter.domain.repository.iris;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sleeved.looter.domain.entity.iris.HashCard;

@Repository
public interface HashCardRepository extends JpaRepository<HashCard, String> {
  Optional<HashCard> findById(String id);
}
