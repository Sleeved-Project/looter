package com.sleeved.looter.infra.processor;

import java.util.List;

import com.sleeved.looter.domain.entity.atlas.Card;

/**
 * Interface générique pour les processeurs de relations de cartes
 */
public interface CardRelationProcessor<T, R> {
  List<R> process(List<T> dtos, Card card);
}