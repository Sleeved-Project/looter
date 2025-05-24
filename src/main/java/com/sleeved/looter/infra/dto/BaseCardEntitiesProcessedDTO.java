package com.sleeved.looter.infra.dto;

import java.util.List;

import com.sleeved.looter.domain.entity.atlas.Ability;
import com.sleeved.looter.domain.entity.atlas.Artist;
import com.sleeved.looter.domain.entity.atlas.Attack;
import com.sleeved.looter.domain.entity.atlas.Legalities;
import com.sleeved.looter.domain.entity.atlas.Rarity;
import com.sleeved.looter.domain.entity.atlas.Subtype;
import com.sleeved.looter.domain.entity.atlas.Type;

import lombok.Data;

@Data
public class BaseCardEntitiesProcessedDTO {
  private Rarity rarity;
  private Artist artist;
  private List<Type> types;
  private List<Subtype> subtypes;
  private List<Ability> abilities;
  private List<Attack> attacks;
  private Legalities legalities;
}
