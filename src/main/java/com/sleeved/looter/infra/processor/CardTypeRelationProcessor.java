package com.sleeved.looter.infra.processor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.entity.atlas.CardType;
import com.sleeved.looter.domain.entity.atlas.Type;
import com.sleeved.looter.domain.service.TypeService;
import com.sleeved.looter.infra.mapper.CardTypeMapper;
import com.sleeved.looter.infra.mapper.TypeMapper;

@Component
public class CardTypeRelationProcessor implements CardRelationProcessor<String, CardType> {

  private final TypeMapper typeMapper;
  private final TypeService typeService;
  private final CardTypeMapper cardTypeMapper;

  public CardTypeRelationProcessor(
      TypeMapper typeMapper,
      TypeService typeService,
      CardTypeMapper cardTypeMapper) {
    this.typeMapper = typeMapper;
    this.typeService = typeService;
    this.cardTypeMapper = cardTypeMapper;
  }

  @Override
  public List<CardType> process(List<String> typesDTO, Card card) {
    List<CardType> cardTypes = new ArrayList<>();
    if (typesDTO == null || typesDTO.isEmpty()) {
      return cardTypes;
    }

    List<Type> typesToFind = typeMapper.toListEntity(typesDTO);

    for (Type typeToFind : typesToFind) {
      Type typeFound = typeService.getByLabel(typeToFind);
      CardType cardType = cardTypeMapper.toEntity(typeFound, card);
      if (cardType == null) {
        continue;
      }
      cardTypes.add(cardType);
    }

    return cardTypes;
  }
}
