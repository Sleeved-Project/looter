package com.sleeved.looter.infra.processor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sleeved.looter.domain.entity.atlas.Type;
import com.sleeved.looter.domain.entity.atlas.Weakness;
import com.sleeved.looter.domain.service.TypeService;
import com.sleeved.looter.infra.dto.WeaknessDTO;
import com.sleeved.looter.infra.mapper.TypeMapper;
import com.sleeved.looter.infra.mapper.WeaknessMapper;

@Service
public class WeaknessProcessor {

  private final TypeService typeService;
  private final TypeMapper typeMapper;
  private final WeaknessMapper weaknessMapper;

  public WeaknessProcessor(TypeService typeService, TypeMapper typeMapper, WeaknessMapper weaknessMapper) {
    this.typeService = typeService;
    this.typeMapper = typeMapper;
    this.weaknessMapper = weaknessMapper;
  }

  public List<Weakness> processFromDTOs(List<WeaknessDTO> weaknessesDTO) {
    List<Weakness> weaknesses = new ArrayList<>();
    if (weaknessesDTO == null) {
      return weaknesses;
    }

    for (WeaknessDTO weaknessDTO : weaknessesDTO) {
      Type weaknessTypeToFind = typeMapper.toEntity(weaknessDTO.getType());
      Type weaknessTypeFound = typeService.getByLabel(weaknessTypeToFind);
      Weakness weakness = weaknessMapper.toEntity(weaknessDTO, weaknessTypeFound);
      weaknesses.add(weakness);
    }

    return weaknesses;
  }
}
