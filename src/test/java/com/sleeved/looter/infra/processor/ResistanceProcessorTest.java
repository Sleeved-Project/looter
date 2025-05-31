package com.sleeved.looter.infra.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sleeved.looter.domain.entity.atlas.Resistance;
import com.sleeved.looter.domain.entity.atlas.Type;
import com.sleeved.looter.domain.service.TypeService;
import com.sleeved.looter.infra.dto.ResistanceDTO;
import com.sleeved.looter.infra.mapper.ResistanceMapper;
import com.sleeved.looter.infra.mapper.TypeMapper;
import com.sleeved.looter.mock.domain.ResistanceMock;
import com.sleeved.looter.mock.domain.TypeMock;
import com.sleeved.looter.mock.infra.ResistanceDTOMock;

@ExtendWith(MockitoExtension.class)
class ResistanceProcessorTest {

  @Mock
  private TypeService typeService;

  @Mock
  private TypeMapper typeMapper;

  @Mock
  private ResistanceMapper resistanceMapper;

  @InjectMocks
  private ResistanceProcessor resistanceProcessor;

  private ResistanceDTO resistanceDTO;
  private Type mappedType;
  private Type foundType;
  private Resistance mappedResistance;

  @BeforeEach
  void setUp() {
    resistanceDTO = ResistanceDTOMock.createMockResistanceDTO("Fighting", "-30");
    mappedType = TypeMock.createMockType("Fighting");
    foundType = TypeMock.createMockTypeSavedInDb(1, "Fighting");
    mappedResistance = ResistanceMock.createMockResistance(foundType, "-30");
  }

  @Test
  void processFromDTOs_shouldReturnEmptyList_whenResistanceDTOListIsNull() {
    List<Resistance> result = resistanceProcessor.processFromDTOs(null);

    assertThat(result).isNotNull();
    assertThat(result).isEmpty();
  }

  @Test
  void processFromDTOs_shouldReturnEmptyList_whenResistanceDTOListIsEmpty() {
    List<Resistance> result = resistanceProcessor.processFromDTOs(Collections.emptyList());

    assertThat(result).isNotNull();
    assertThat(result).isEmpty();
  }

  @Test
  void processFromDTOs_shouldProcessSingleResistanceDTO_whenListContainsOneItem() {
    List<ResistanceDTO> resistanceDTOs = Collections.singletonList(resistanceDTO);

    when(typeMapper.toEntity(resistanceDTO.getType())).thenReturn(mappedType);
    when(typeService.getByLabel(mappedType)).thenReturn(foundType);
    when(resistanceMapper.toEntity(resistanceDTO, foundType)).thenReturn(mappedResistance);

    List<Resistance> result = resistanceProcessor.processFromDTOs(resistanceDTOs);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0)).isEqualTo(mappedResistance);
    assertThat(result.get(0).getType()).isEqualTo(foundType);
    assertThat(result.get(0).getValue()).isEqualTo("-30");

    verify(typeMapper).toEntity(resistanceDTO.getType());
    verify(typeService).getByLabel(mappedType);
    verify(resistanceMapper).toEntity(resistanceDTO, foundType);
  }

  @Test
  void processFromDTOs_shouldProcessMultipleResistanceDTOs_whenListContainsMultipleItems() {
    ResistanceDTO resistanceDTO2 = ResistanceDTOMock.createMockResistanceDTO("Psychic", "-20");
    List<ResistanceDTO> resistanceDTOs = Arrays.asList(resistanceDTO, resistanceDTO2);

    Type mappedType2 = TypeMock.createMockType("Psychic");
    Type foundType2 = TypeMock.createMockTypeSavedInDb(2, "Psychic");
    Resistance mappedResistance2 = ResistanceMock.createMockResistance(foundType2, "-20");

    when(typeMapper.toEntity(resistanceDTO.getType())).thenReturn(mappedType);
    when(typeService.getByLabel(mappedType)).thenReturn(foundType);
    when(resistanceMapper.toEntity(resistanceDTO, foundType)).thenReturn(mappedResistance);

    when(typeMapper.toEntity(resistanceDTO2.getType())).thenReturn(mappedType2);
    when(typeService.getByLabel(mappedType2)).thenReturn(foundType2);
    when(resistanceMapper.toEntity(resistanceDTO2, foundType2)).thenReturn(mappedResistance2);

    List<Resistance> result = resistanceProcessor.processFromDTOs(resistanceDTOs);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);
    assertThat(result.get(0)).isEqualTo(mappedResistance);
    assertThat(result.get(1)).isEqualTo(mappedResistance2);

    verify(typeMapper, times(2)).toEntity(any(String.class));
    verify(typeService, times(2)).getByLabel(any(Type.class));
    verify(resistanceMapper, times(2)).toEntity(any(ResistanceDTO.class), any(Type.class));
  }

  @Test
  void processFromDTOs_shouldHandleDifferentResistanceValues() {
    ResistanceDTO resistanceDTO1 = ResistanceDTOMock.createMockResistanceDTO("Fighting", "-30");
    ResistanceDTO resistanceDTO2 = ResistanceDTOMock.createMockResistanceDTO("Water", "-20");
    List<ResistanceDTO> resistanceDTOs = Arrays.asList(resistanceDTO1, resistanceDTO2);

    Type mappedType1 = TypeMock.createMockType("Fighting");
    Type foundType1 = TypeMock.createMockTypeSavedInDb(1, "Fighting");
    Resistance mappedResistance1 = ResistanceMock.createMockResistance(foundType1, "-30");

    Type mappedType2 = TypeMock.createMockType("Water");
    Type foundType2 = TypeMock.createMockTypeSavedInDb(2, "Water");
    Resistance mappedResistance2 = ResistanceMock.createMockResistance(foundType2, "-20");

    when(typeMapper.toEntity(resistanceDTO1.getType())).thenReturn(mappedType1);
    when(typeService.getByLabel(mappedType1)).thenReturn(foundType1);
    when(resistanceMapper.toEntity(resistanceDTO1, foundType1)).thenReturn(mappedResistance1);

    when(typeMapper.toEntity(resistanceDTO2.getType())).thenReturn(mappedType2);
    when(typeService.getByLabel(mappedType2)).thenReturn(foundType2);
    when(resistanceMapper.toEntity(resistanceDTO2, foundType2)).thenReturn(mappedResistance2);

    List<Resistance> result = resistanceProcessor.processFromDTOs(resistanceDTOs);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);
    assertThat(result.get(0).getValue()).isEqualTo("-30");
    assertThat(result.get(1).getValue()).isEqualTo("-20");
  }

  @Test
  void processFromDTOs_shouldUseCorrectType_forEachResistance() {
    ResistanceDTO fightingResistanceDTO = ResistanceDTOMock.createMockResistanceDTO("Fighting", "-30");
    ResistanceDTO psychicResistanceDTO = ResistanceDTOMock.createMockResistanceDTO("Psychic", "-30");
    List<ResistanceDTO> resistanceDTOs = Arrays.asList(fightingResistanceDTO, psychicResistanceDTO);

    Type fightingType = TypeMock.createMockType("Fighting");
    Type psychicType = TypeMock.createMockType("Psychic");

    Type foundFightingType = TypeMock.createMockTypeSavedInDb(1, "Fighting");
    Type foundPsychicType = TypeMock.createMockTypeSavedInDb(2, "Psychic");

    Resistance fightingResistance = ResistanceMock.createMockResistance(foundFightingType, "-30");
    Resistance psychicResistance = ResistanceMock.createMockResistance(foundPsychicType, "-30");

    when(typeMapper.toEntity("Fighting")).thenReturn(fightingType);
    when(typeMapper.toEntity("Psychic")).thenReturn(psychicType);

    when(typeService.getByLabel(fightingType)).thenReturn(foundFightingType);
    when(typeService.getByLabel(psychicType)).thenReturn(foundPsychicType);

    when(resistanceMapper.toEntity(fightingResistanceDTO, foundFightingType)).thenReturn(fightingResistance);
    when(resistanceMapper.toEntity(psychicResistanceDTO, foundPsychicType)).thenReturn(psychicResistance);

    List<Resistance> result = resistanceProcessor.processFromDTOs(resistanceDTOs);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);
    assertThat(result.get(0).getType().getLabel()).isEqualTo("Fighting");
    assertThat(result.get(1).getType().getLabel()).isEqualTo("Psychic");
  }
}