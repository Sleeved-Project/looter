package com.sleeved.looter.infra.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sleeved.looter.domain.entity.atlas.Type;

@ExtendWith(MockitoExtension.class)
public class TypeMapperTest {

  @InjectMocks
  private TypeMapper mapper;

  @Test
  void toEntity_shouldMapValidLabel() {
    String typeLabel = "Creature";

    Type result = mapper.toEntity(typeLabel);

    assertThat(result).isNotNull();
    assertThat(result.getLabel()).isEqualTo("Creature");
  }

  @Test
  void toEntity_shouldTrimLabel() {
    String typeLabel = "  Artifact  ";

    Type result = mapper.toEntity(typeLabel);

    assertThat(result).isNotNull();
    assertThat(result.getLabel()).isEqualTo("Artifact");
  }

  @Test
  void toEntity_shouldHandleNullLabel() {
    Type result = mapper.toEntity(null);

    assertThat(result).isNotNull();
    assertThat(result.getLabel()).isEqualTo("UNKNOWN");
  }

  @Test
  void toEntity_shouldHandleEmptyLabel() {
    // Given
    String typeLabel = "";

    // When
    Type result = mapper.toEntity(typeLabel);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getLabel()).isEqualTo("UNKNOWN");
  }

  @Test
  void toListEntity_shouldMapMultipleLabels() {
    List<String> typeLabels = Arrays.asList("Creature", "Artifact", "Land");

    List<Type> results = mapper.toListEntity(typeLabels);

    assertThat(results).hasSize(3);
    assertThat(results.get(0).getLabel()).isEqualTo("Creature");
    assertThat(results.get(1).getLabel()).isEqualTo("Artifact");
    assertThat(results.get(2).getLabel()).isEqualTo("Land");
  }

  @Test
  void toListEntity_shouldHandleNullList() {
    List<Type> results = mapper.toListEntity(null);

    assertThat(results).isEmpty();
  }

  @Test
  void toListEntity_shouldHandleEmptyList() {
    List<String> emptyList = List.of();

    List<Type> results = mapper.toListEntity(emptyList);

    assertThat(results).isEmpty();
  }
}
