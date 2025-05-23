package com.sleeved.looter.mock.infra;

import com.sleeved.looter.infra.dto.LegalitiesDTO;

public class LegalitiesDTOMock {
  public static LegalitiesDTO createMockLegalitiesDTO(String standard, String expanded, String unlimited) {
    LegalitiesDTO legalitiesDTO = new LegalitiesDTO();
    legalitiesDTO.setStandard(standard);
    legalitiesDTO.setExpanded(expanded);
    legalitiesDTO.setUnlimited(unlimited);
    return legalitiesDTO;
  }
}
