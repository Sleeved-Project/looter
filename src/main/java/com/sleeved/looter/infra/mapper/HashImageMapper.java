package com.sleeved.looter.infra.mapper;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.sleeved.looter.infra.dto.CardImageDTO;
import com.sleeved.looter.infra.dto.HashImageDTO;

@Component
public class HashImageMapper {

    public HashImageDTO toHashImageDTO(CardImageDTO item, JsonNode response) {
      if (response == null || response.isEmpty()) {
          return null;
      }
      
      HashImageDTO hashImageDTO = new HashImageDTO();
      hashImageDTO.setId(item.getCardId());
      hashImageDTO.setHash(response.get("hash").asText());
      return hashImageDTO;
    }
}
