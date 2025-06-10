package com.sleeved.looter.domain.repository.atlas;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sleeved.looter.domain.entity.atlas.Card;

public interface CardRepository extends JpaRepository<Card, String> {
    List<Card> findByImageLargeIsNotNull();
}
