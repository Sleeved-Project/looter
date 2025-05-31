package com.sleeved.looter.infra.processor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sleeved.looter.domain.entity.atlas.Type;
import com.sleeved.looter.domain.entity.atlas.Resistance;
import com.sleeved.looter.domain.service.TypeService;
import com.sleeved.looter.infra.dto.ResistanceDTO;
import com.sleeved.looter.infra.mapper.TypeMapper;
import com.sleeved.looter.infra.mapper.ResistanceMapper;

@Service
public class ResistanceProcessor {

  private final TypeService typeService;
  private final TypeMapper typeMapper;
  private final ResistanceMapper resistanceMapper;

  public ResistanceProcessor(TypeService typeService, TypeMapper typeMapper, ResistanceMapper resistanceMapper) {
    this.typeService = typeService;
    this.typeMapper = typeMapper;
    this.resistanceMapper = resistanceMapper;
  }

  public List<Resistance> processFromDTOs(List<ResistanceDTO> resistancesDTO) {
    List<Resistance> resistances = new ArrayList<>();
    if (resistancesDTO == null) {
      return resistances;
    }

    for (ResistanceDTO resistanceDTO : resistancesDTO) {
      Type resistanceTypeToFind = typeMapper.toEntity(resistanceDTO.getType());
      Type resistanceTypeFound = typeService.getByLabel(resistanceTypeToFind);
      Resistance resistance = resistanceMapper.toEntity(resistanceDTO, resistanceTypeFound);
      resistances.add(resistance);
    }

    return resistances;
  }
}