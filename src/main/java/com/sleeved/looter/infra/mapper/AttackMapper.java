package com.sleeved.looter.infra.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.sleeved.looter.domain.entity.atlas.Attack;
import com.sleeved.looter.infra.dto.AttackDTO;

@Component
public class AttackMapper {
  public Attack toEntity(AttackDTO attackDTO) {
    Attack attack = new Attack();
    attack.setName(attackDTO.getName());
    attack.setDamage(attackDTO.getDamage());
    attack.setConvertedEnergyCost(attackDTO.getConvertedEnergyCost());
    attack.setText(attackDTO.getText());
    return attack;
  }

  public List<Attack> toListEntity(List<AttackDTO> attackDTOs) {
    if (attackDTOs == null || attackDTOs.isEmpty()) {
      return List.of();
    }
    return attackDTOs.stream()
        .map(attackDTO -> this.toEntity(attackDTO))
        .toList();
  }
}
