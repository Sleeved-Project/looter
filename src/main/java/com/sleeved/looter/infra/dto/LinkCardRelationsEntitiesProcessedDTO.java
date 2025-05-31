
package com.sleeved.looter.infra.dto;

import java.util.List;

import com.sleeved.looter.domain.entity.atlas.CardAbility;
import com.sleeved.looter.domain.entity.atlas.CardAttack;
import com.sleeved.looter.domain.entity.atlas.CardResistance;
import com.sleeved.looter.domain.entity.atlas.CardSubtype;
import com.sleeved.looter.domain.entity.atlas.CardType;
import com.sleeved.looter.domain.entity.atlas.CardWeakness;

import lombok.Data;

@Data
public class LinkCardRelationsEntitiesProcessedDTO {
  private List<CardAbility> cardAbilities;
  private List<CardAttack> cardAttacks;
  private List<CardResistance> cardResistances;
  private List<CardSubtype> cardSubtypes;
  private List<CardType> cardTypes;
  private List<CardWeakness> cardWeaknesses;
}
