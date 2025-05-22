package com.sleeved.looter.mock;

import java.util.ArrayList;
import java.util.List;

import com.sleeved.looter.infra.dto.AbilityDTO;

public class AbilityDTOMock {
  public static AbilityDTO createMockAbilityDTO(String name, String text, String type) {
    AbilityDTO abilityDTO = new AbilityDTO();
    abilityDTO.setName(name);
    abilityDTO.setText(text);
    abilityDTO.setType(type);
    return abilityDTO;
  }

  public static List<AbilityDTO> createMockAbilityDTOsList(int count) {
    List<AbilityDTO> abilityDTOs = new ArrayList<>();

    for (int i = 0; i < count; i++) {
      AbilityDTO abilityDTO = new AbilityDTO();
      abilityDTO.setName("Name " + (i + 1));
      abilityDTO.setText("Text " + (i + 1));
      abilityDTO.setType("Type " + (i + 1));
      abilityDTOs.add(abilityDTO);
    }

    return abilityDTOs;
  }

}
