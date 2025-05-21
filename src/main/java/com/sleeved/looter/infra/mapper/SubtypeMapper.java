package com.sleeved.looter.infra.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.sleeved.looter.domain.entity.atlas.Subtype;

@Component
public class SubtypeMapper {
  public Subtype toEntity(String subtypeLabel) {
    Subtype subtype = new Subtype();
    if (subtypeLabel != null && !subtypeLabel.isBlank()) {
      subtype.setLabel(subtypeLabel.trim());
    } else {
      subtype.setLabel("UNKNOWN");
    }
    return subtype;
  }

  public List<Subtype> toListEntity(List<String> subtypeLabels) {
    if (subtypeLabels == null || subtypeLabels.isEmpty()) {
      return List.of();
    }
    return subtypeLabels.stream()
        .map(label -> this.toEntity(label))
        .toList();
  }
}
