package com.sleeved.looter.infra.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.sleeved.looter.domain.entity.atlas.Type;

@Component
public class TypeMapper {
  public Type toEntity(String typeLabel) {
    Type type = new Type();
    if (typeLabel != null && !typeLabel.isBlank()) {
      type.setLabel(typeLabel.trim());
    } else {
      type.setLabel("UNKNOWN");
    }
    return type;
  }

  public List<Type> toListEntity(List<String> typeLabels) {
    if (typeLabels == null || typeLabels.isEmpty()) {
      return List.of();
    }
    return typeLabels.stream()
        .map(label -> this.toEntity(label))
        .toList();
  }
}
