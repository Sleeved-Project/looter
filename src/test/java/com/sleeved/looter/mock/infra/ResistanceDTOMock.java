package com.sleeved.looter.mock.infra;

import com.sleeved.looter.infra.dto.ResistanceDTO;

public class ResistanceDTOMock {

  public static ResistanceDTO createMockResistanceDTO(String type, String value) {
    ResistanceDTO dto = new ResistanceDTO();
    dto.setType(type);
    dto.setValue(value);
    return dto;
  }
}