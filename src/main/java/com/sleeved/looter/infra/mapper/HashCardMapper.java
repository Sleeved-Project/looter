package com.sleeved.looter.infra.mapper;

import org.springframework.stereotype.Component;

import com.sleeved.looter.domain.entity.iris.HashCard;
import com.sleeved.looter.infra.dto.HashImageDTO;

@Component
public class HashCardMapper {
  
  public HashCard toEntity(HashImageDTO dto) {
    if (dto == null) {
      return null;
    }
    
    HashCard entity = new HashCard();
    entity.setId(dto.getId());
    entity.setHash(dto.getHash());
    
    return entity;
  }
  
  public HashImageDTO toDto(HashCard entity) {
    if (entity == null) {
      return null;
    }
    
    HashImageDTO dto = new HashImageDTO();
    dto.setId(entity.getId());
    dto.setHash(entity.getHash());
    
    return dto;
  }
}