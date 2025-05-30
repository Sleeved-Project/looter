package com.sleeved.looter.infra.dto;

import com.sleeved.looter.domain.entity.atlas.Card;

import lombok.Data;

@Data
public class CardEntitiesProcessedDTO {
  private Card card;
}
