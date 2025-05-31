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

import com.sleeved.looter.domain.entity.atlas.Type;
import com.sleeved.looter.domain.entity.atlas.Weakness;
import com.sleeved.looter.domain.service.TypeService;
import com.sleeved.looter.infra.dto.WeaknessDTO;
import com.sleeved.looter.infra.mapper.TypeMapper;
import com.sleeved.looter.infra.mapper.WeaknessMapper;
import com.sleeved.looter.mock.domain.TypeMock;
import com.sleeved.looter.mock.domain.WeaknessMock;
import com.sleeved.looter.mock.infra.WeaknessDTOMock;

@ExtendWith(MockitoExtension.class)
class WeaknessProcessorTest {

  @Mock
  private TypeService typeService;

  @Mock
  private TypeMapper typeMapper;

  @Mock
  private WeaknessMapper weaknessMapper;

  @InjectMocks
  private WeaknessProcessor weaknessProcessor;

  private WeaknessDTO weaknessDTO;
  private Type mappedType;
  private Type foundType;
  private Weakness mappedWeakness;

  @BeforeEach
  void setUp() {
    weaknessDTO = WeaknessDTOMock.createMockWeaknessDTO("Fire", "×2");
    mappedType = TypeMock.createMockType("Fire");
    foundType = TypeMock.createMockTypeSavedInDb(1, "Fire");
    mappedWeakness = WeaknessMock.createMockWeakness(foundType, "×2");
  }

  @Test
  void processFromDTOs_shouldReturnEmptyList_whenWeaknessDTOListIsNull() {
    List<Weakness> result = weaknessProcessor.processFromDTOs(null);

    assertThat(result).isNotNull();
    assertThat(result).isEmpty();
  }

  @Test
  void processFromDTOs_shouldReturnEmptyList_whenWeaknessDTOListIsEmpty() {
    List<Weakness> result = weaknessProcessor.processFromDTOs(Collections.emptyList());

    assertThat(result).isNotNull();
    assertThat(result).isEmpty();
  }

  @Test
  void processFromDTOs_shouldProcessSingleWeaknessDTO_whenListContainsOneItem() {
    List<WeaknessDTO> weaknessDTOs = Collections.singletonList(weaknessDTO);

    when(typeMapper.toEntity(weaknessDTO.getType())).thenReturn(mappedType);
    when(typeService.getByLabel(mappedType)).thenReturn(foundType);
    when(weaknessMapper.toEntity(weaknessDTO, foundType)).thenReturn(mappedWeakness);

    List<Weakness> result = weaknessProcessor.processFromDTOs(weaknessDTOs);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0)).isEqualTo(mappedWeakness);
    assertThat(result.get(0).getType()).isEqualTo(foundType);
    assertThat(result.get(0).getValue()).isEqualTo("×2");

    verify(typeMapper).toEntity(weaknessDTO.getType());
    verify(typeService).getByLabel(mappedType);
    verify(weaknessMapper).toEntity(weaknessDTO, foundType);
  }

  @Test
  void processFromDTOs_shouldProcessMultipleWeaknessDTOs_whenListContainsMultipleItems() {
    WeaknessDTO weaknessDTO2 = WeaknessDTOMock.createMockWeaknessDTO("Water", "×2");
    List<WeaknessDTO> weaknessDTOs = Arrays.asList(weaknessDTO, weaknessDTO2);

    Type mappedType2 = TypeMock.createMockType("Water");
    Type foundType2 = TypeMock.createMockTypeSavedInDb(2, "Water");
    Weakness mappedWeakness2 = WeaknessMock.createMockWeakness(foundType2, "×2");

    when(typeMapper.toEntity(weaknessDTO.getType())).thenReturn(mappedType);
    when(typeService.getByLabel(mappedType)).thenReturn(foundType);
    when(weaknessMapper.toEntity(weaknessDTO, foundType)).thenReturn(mappedWeakness);

    when(typeMapper.toEntity(weaknessDTO2.getType())).thenReturn(mappedType2);
    when(typeService.getByLabel(mappedType2)).thenReturn(foundType2);
    when(weaknessMapper.toEntity(weaknessDTO2, foundType2)).thenReturn(mappedWeakness2);

    List<Weakness> result = weaknessProcessor.processFromDTOs(weaknessDTOs);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);
    assertThat(result.get(0)).isEqualTo(mappedWeakness);
    assertThat(result.get(1)).isEqualTo(mappedWeakness2);

    verify(typeMapper, times(2)).toEntity(any(String.class));
    verify(typeService, times(2)).getByLabel(any(Type.class));
    verify(weaknessMapper, times(2)).toEntity(any(WeaknessDTO.class), any(Type.class));
  }

  @Test
  void processFromDTOs_shouldHandleDifferentWeaknessValues() {
    WeaknessDTO weaknessDTO1 = WeaknessDTOMock.createMockWeaknessDTO("Fire", "×2");
    WeaknessDTO weaknessDTO2 = WeaknessDTOMock.createMockWeaknessDTO("Water", "×3");
    List<WeaknessDTO> weaknessDTOs = Arrays.asList(weaknessDTO1, weaknessDTO2);

    Type mappedType1 = TypeMock.createMockType("Fire");
    Type foundType1 = TypeMock.createMockTypeSavedInDb(1, "Fire");
    Weakness mappedWeakness1 = WeaknessMock.createMockWeakness(foundType1, "×2");

    Type mappedType2 = TypeMock.createMockType("Water");
    Type foundType2 = TypeMock.createMockTypeSavedInDb(2, "Water");
    Weakness mappedWeakness2 = WeaknessMock.createMockWeakness(foundType2, "×3");

    when(typeMapper.toEntity(weaknessDTO1.getType())).thenReturn(mappedType1);
    when(typeService.getByLabel(mappedType1)).thenReturn(foundType1);
    when(weaknessMapper.toEntity(weaknessDTO1, foundType1)).thenReturn(mappedWeakness1);

    when(typeMapper.toEntity(weaknessDTO2.getType())).thenReturn(mappedType2);
    when(typeService.getByLabel(mappedType2)).thenReturn(foundType2);
    when(weaknessMapper.toEntity(weaknessDTO2, foundType2)).thenReturn(mappedWeakness2);

    List<Weakness> result = weaknessProcessor.processFromDTOs(weaknessDTOs);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);
    assertThat(result.get(0).getValue()).isEqualTo("×2");
    assertThat(result.get(1).getValue()).isEqualTo("×3");
  }

  @Test
  void processFromDTOs_shouldUseCorrectType_forEachWeakness() {
    WeaknessDTO fireWeaknessDTO = WeaknessDTOMock.createMockWeaknessDTO("Fire", "×2");
    WeaknessDTO waterWeaknessDTO = WeaknessDTOMock.createMockWeaknessDTO("Water", "×2");
    List<WeaknessDTO> weaknessDTOs = Arrays.asList(fireWeaknessDTO, waterWeaknessDTO);

    Type fireType = TypeMock.createMockType("Fire");
    Type waterType = TypeMock.createMockType("Water");

    Type foundFireType = TypeMock.createMockTypeSavedInDb(1, "Fire");
    Type foundWaterType = TypeMock.createMockTypeSavedInDb(2, "Water");

    Weakness fireWeakness = WeaknessMock.createMockWeakness(foundFireType, "×2");
    Weakness waterWeakness = WeaknessMock.createMockWeakness(foundWaterType, "×2");

    when(typeMapper.toEntity("Fire")).thenReturn(fireType);
    when(typeMapper.toEntity("Water")).thenReturn(waterType);

    when(typeService.getByLabel(fireType)).thenReturn(foundFireType);
    when(typeService.getByLabel(waterType)).thenReturn(foundWaterType);

    when(weaknessMapper.toEntity(fireWeaknessDTO, foundFireType)).thenReturn(fireWeakness);
    when(weaknessMapper.toEntity(waterWeaknessDTO, foundWaterType)).thenReturn(waterWeakness);

    List<Weakness> result = weaknessProcessor.processFromDTOs(weaknessDTOs);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);
    assertThat(result.get(0).getType().getLabel()).isEqualTo("Fire");
    assertThat(result.get(1).getType().getLabel()).isEqualTo("Water");
  }
}