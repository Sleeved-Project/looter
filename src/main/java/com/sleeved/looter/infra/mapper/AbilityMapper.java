package com.sleeved.looter.infra.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.sleeved.looter.domain.entity.atlas.Ability;
import com.sleeved.looter.infra.dto.AbilityDTO;

@Component
public class AbilityMapper {
  public Ability toEntity(AbilityDTO abilityDTO) {
    if (abilityDTO == null) {
      return null;
    }

    Ability ability = new Ability();
    ability.setName(abilityDTO.getName());
    ability.setText(abilityDTO.getText());
    ability.setType(abilityDTO.getType());
    return ability;
  }

  public List<Ability> toListEntity(List<AbilityDTO> AbilityDTOs) {
    if (AbilityDTOs == null || AbilityDTOs.isEmpty()) {
      return List.of();
    }
    return AbilityDTOs.stream()
        .map(this::toEntity)
        .filter(ability -> ability != null)
        .toList();
  }
}
