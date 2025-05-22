package com.sleeved.looter.mock;

import java.util.ArrayList;
import java.util.List;

import com.sleeved.looter.infra.dto.AttackDTO;

public class AttackDTOMock {
  public static AttackDTO createMockAttackDTO(String name, List<String> cost, int convertedEnergyCost, String damage,
      String text) {
    AttackDTO attackDTO = new AttackDTO();
    attackDTO.setName(name);
    attackDTO.setCost(cost);
    attackDTO.setConvertedEnergyCost(convertedEnergyCost);
    attackDTO.setDamage(damage);
    attackDTO.setText(text);
    return attackDTO;
  }

  public static List<AttackDTO> createMockAttackDTOsList(int count) {
    List<AttackDTO> attackDTOs = new ArrayList<>();

    for (int i = 0; i < count; i++) {
      AttackDTO attackDTO = new AttackDTO();
      attackDTO.setName("Name " + (i + 1));
      attackDTO.setCost(List.of("Cost " + (i + 1)));
      attackDTO.setConvertedEnergyCost(i + 1);
      attackDTO.setDamage("Damage " + (i + 1));
      attackDTO.setText("Text " + (i + 1));
      attackDTOs.add(attackDTO);
    }

    return attackDTOs;
  }

}
