package com.sleeved.looter.infra.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sleeved.looter.domain.entity.atlas.Subtype;

@ExtendWith(MockitoExtension.class)
public class SubtypeMapperTest {

  @InjectMocks
  private SubtypeMapper mapper;

  @Test
  void toEntity_shouldMapValidLabel() {
    String subtypeLabel = "Human";

    Subtype result = mapper.toEntity(subtypeLabel);

    assertThat(result).isNotNull();
    assertThat(result.getLabel()).isEqualTo("Human");
  }

  @Test
  void toEntity_shouldTrimLabel() {
    String subtypeLabel = "  Elf  ";

    Subtype result = mapper.toEntity(subtypeLabel);

    assertThat(result).isNotNull();
    assertThat(result.getLabel()).isEqualTo("Elf");
  }

  @Test
  void toEntity_shouldHandleNullLabel() {
    Subtype result = mapper.toEntity(null);

    assertThat(result).isNotNull();
    assertThat(result.getLabel()).isEqualTo("UNKNOWN");
  }

  @Test
  void toEntity_shouldHandleEmptyLabel() {
    String subtypeLabel = "";

    Subtype result = mapper.toEntity(subtypeLabel);

    assertThat(result).isNotNull();
    assertThat(result.getLabel()).isEqualTo("UNKNOWN");
  }

  @Test
  void toListEntity_shouldMapMultipleLabels() {
    List<String> subtypeLabels = Arrays.asList("Human", "Wizard", "Warrior");

    List<Subtype> results = mapper.toListEntity(subtypeLabels);

    assertThat(results).hasSize(3);
    assertThat(results.get(0).getLabel()).isEqualTo("Human");
    assertThat(results.get(1).getLabel()).isEqualTo("Wizard");
    assertThat(results.get(2).getLabel()).isEqualTo("Warrior");
  }

  @Test
  void toListEntity_shouldHandleNullList() {
    List<Subtype> results = mapper.toListEntity(null);

    assertThat(results).isEmpty();
  }

  @Test
  void toListEntity_shouldHandleEmptyList() {
    List<String> emptyList = List.of();

    List<Subtype> results = mapper.toListEntity(emptyList);

    assertThat(results).isEmpty();
  }
}
