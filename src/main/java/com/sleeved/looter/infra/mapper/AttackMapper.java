package com.sleeved.looter.infra.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.sleeved.looter.domain.entity.atlas.Attack;
import com.sleeved.looter.infra.dto.AttackDTO;

@Component
public class AttackMapper {
  public Attack toEntity(AttackDTO attackDTO) {
    if (attackDTO == null) {
      return null;
    }
    Attack attack = new Attack();
    attack.setName(attackDTO.getName());
    attack.setDamage(attackDTO.getDamage() == null || attackDTO.getDamage().isEmpty() ? null : attackDTO.getDamage());
    attack.setText(attackDTO.getText() == null || attackDTO.getText().isEmpty() ? null : attackDTO.getText());
    if (isFreeOrEmptyCost(attackDTO.getCost())) {
      attack.setConvertedEnergyCost(0);
    } else {
      attack.setConvertedEnergyCost(attackDTO.getConvertedEnergyCost());
    }
    return attack;
  }

  public List<Attack> toListEntity(List<AttackDTO> attackDTOs) {
    if (attackDTOs == null || attackDTOs.isEmpty()) {
      return List.of();
    }
    return attackDTOs.stream()
        .map(this::toEntity)
        .filter(attack -> attack != null)
        .toList();
  }

  protected boolean isFreeOrEmptyCost(List<String> costs) {
    return costs == null ||
        costs.isEmpty() ||
        (costs.size() == 1 && "Free".equals(costs.get(0)));
  }
}
