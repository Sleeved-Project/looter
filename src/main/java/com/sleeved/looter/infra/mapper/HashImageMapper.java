package com.sleeved.looter.infra.mapper;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.sleeved.looter.domain.entity.iris.HashCard;
import com.sleeved.looter.infra.dto.CardImageDTO;

@Component
public class HashImageMapper {

    public HashCard toHashCard(CardImageDTO item, JsonNode response) {
      if (response == null || response.isEmpty()) {
          return null;
      }
      
      HashCard hashCard = new HashCard();
      hashCard.setId(item.getCardId());
      hashCard.setHash(response.get("hash").asText());
      return hashCard;
    }
}
