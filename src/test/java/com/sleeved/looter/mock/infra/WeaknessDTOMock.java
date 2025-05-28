package com.sleeved.looter.mock.infra;

import com.sleeved.looter.infra.dto.WeaknessDTO;

public class WeaknessDTOMock {

  public static WeaknessDTO createMockWeaknessDTO(String type, String value) {
    WeaknessDTO dto = new WeaknessDTO();
    dto.setType(type);
    dto.setValue(value);
    return dto;
  }
}