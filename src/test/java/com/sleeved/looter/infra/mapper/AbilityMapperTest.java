package com.sleeved.looter.infra.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sleeved.looter.domain.entity.atlas.Ability;
import com.sleeved.looter.infra.dto.AbilityDTO;
import com.sleeved.looter.mock.infra.AbilityDTOMock;

@ExtendWith(MockitoExtension.class)
public class AbilityMapperTest {

  @InjectMocks
  private AbilityMapper mapper;

  @Test
  void toEntity_shouldMapValidDTO() {
    AbilityDTO dto = AbilityDTOMock.createMockAbilityDTO("Flying",
        "This creature can only be blocked by creatures with flying.",
        "keyword");

    Ability result = mapper.toEntity(dto);

    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo("Flying");
    assertThat(result.getText()).isEqualTo("This creature can only be blocked by creatures with flying.");
    assertThat(result.getType()).isEqualTo("keyword");
  }

  @Test
  void toEntity_shouldHandleNullDTO() {
    Ability result = mapper.toEntity(null);

    assertThat(result).isNull();
  }

  @Test
  void toEntity_shouldHandleDTOWithNullValues() {
    AbilityDTO dto = AbilityDTOMock.createMockAbilityDTO(null, null, null);

    Ability result = mapper.toEntity(dto);

    assertThat(result).isNotNull();
    assertThat(result.getName()).isNull();
    assertThat(result.getText()).isNull();
    assertThat(result.getType()).isNull();
  }

  @Test
  void toEntity_shouldMapDTOWithPartialValues() {
    AbilityDTO dto = AbilityDTOMock.createMockAbilityDTO("First Strike", null, null);

    Ability result = mapper.toEntity(dto);

    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo("First Strike");
    assertThat(result.getText()).isNull();
    assertThat(result.getType()).isNull();
  }

  @Test
  void toListEntity_shouldMapMultipleAbilities() {
    List<AbilityDTO> dtos = AbilityDTOMock.createMockAbilityDTOsList(2);

    List<Ability> results = mapper.toListEntity(dtos);

    assertThat(results).hasSize(2);

    assertThat(results.get(0).getName()).isEqualTo("Name 1");
    assertThat(results.get(0).getText()).isEqualTo("Text 1");
    assertThat(results.get(0).getType()).isEqualTo("Type 1");

    assertThat(results.get(1).getName()).isEqualTo("Name 2");
    assertThat(results.get(1).getText()).isEqualTo("Text 2");
    assertThat(results.get(1).getType()).isEqualTo("Type 2");
  }

  @Test
  void toListEntity_shouldHandleNullList() {
    List<Ability> results = mapper.toListEntity(null);

    assertThat(results).isEmpty();
  }

  @Test
  void toListEntity_shouldHandleEmptyList() {
    List<AbilityDTO> emptyList = AbilityDTOMock.createMockAbilityDTOsList(0);

    List<Ability> results = mapper.toListEntity(emptyList);

    assertThat(results).isEmpty();
  }

  @Test
  void toListEntity_shouldHandleListWithMixedElements() {
    List<AbilityDTO> dtos = new ArrayList<>(AbilityDTOMock.createMockAbilityDTOsList(1));
    dtos.add(null);

    List<Ability> results = mapper.toListEntity(dtos);

    assertThat(results).hasSize(1);
    assertThat(results.get(0).getName()).isEqualTo("Name 1");
    assertThat(results.get(0).getText()).isEqualTo("Text 1");
    assertThat(results.get(0).getType()).isEqualTo("Type 1");
  }
}
