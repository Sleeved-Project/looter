package com.sleeved.looter.infra.processor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.entity.atlas.CardSubtype;
import com.sleeved.looter.domain.entity.atlas.Subtype;
import com.sleeved.looter.domain.service.SubtypeService;
import com.sleeved.looter.infra.mapper.CardSubtypeMapper;
import com.sleeved.looter.infra.mapper.SubtypeMapper;

@Component
public class CardSubtypeRelationProcessor implements CardRelationProcessor<String, CardSubtype> {

  private final SubtypeMapper subtypeMapper;
  private final SubtypeService subtypeService;
  private final CardSubtypeMapper cardSubtypeMapper;

  public CardSubtypeRelationProcessor(
      SubtypeMapper subtypeMapper,
      SubtypeService subtypeService,
      CardSubtypeMapper cardSubtypeMapper) {
    this.subtypeMapper = subtypeMapper;
    this.subtypeService = subtypeService;
    this.cardSubtypeMapper = cardSubtypeMapper;
  }

  @Override
  public List<CardSubtype> process(List<String> subtypesDTO, Card card) {
    List<CardSubtype> cardSubtypes = new ArrayList<>();
    if (subtypesDTO == null || subtypesDTO.isEmpty()) {
      return cardSubtypes;
    }

    List<Subtype> subtypesToFind = subtypeMapper.toListEntity(subtypesDTO);

    for (Subtype subtypeToFind : subtypesToFind) {
      Subtype subtypeFound = subtypeService.getByLabel(subtypeToFind.getLabel());
      CardSubtype cardSubtype = cardSubtypeMapper.toEntity(subtypeFound, card);
      if (cardSubtype == null) {
        continue;
      }
      cardSubtypes.add(cardSubtype);
    }

    return cardSubtypes;
  }
}