package com.sleeved.looter.infra.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sleeved.looter.domain.entity.atlas.Type;
import com.sleeved.looter.domain.entity.atlas.Weakness;
import com.sleeved.looter.infra.dto.WeaknessDTO;
import com.sleeved.looter.mock.domain.TypeMock;
import com.sleeved.looter.mock.infra.WeaknessDTOMock;

@ExtendWith(MockitoExtension.class)
public class WeaknessMapperTest {

  @InjectMocks
  private WeaknessMapper mapper;

  @Test
  void toEntity_shouldMapValidDTO() {
    WeaknessDTO dto = WeaknessDTOMock.createMockWeaknessDTO("Fire", "×2");
    Type weaknessType = TypeMock.createMockType("Fire");

    Weakness result = mapper.toEntity(dto, weaknessType);

    assertThat(result).isNotNull();
    assertThat(result.getValue()).isEqualTo("×2");
    assertThat(result.getType()).isEqualTo(weaknessType);
    assertThat(result.getType().getLabel()).isEqualTo("Fire");
  }

  @Test
  void toEntity_shouldHandleTypeWithId() {
    WeaknessDTO dto = WeaknessDTOMock.createMockWeaknessDTO("Water", "×2");
    Type weaknessType = TypeMock.createMockTypeSavedInDb(123, "Water");

    Weakness result = mapper.toEntity(dto, weaknessType);

    assertThat(result).isNotNull();
    assertThat(result.getValue()).isEqualTo("×2");
    assertThat(result.getType()).isEqualTo(weaknessType);
    assertThat(result.getType().getId()).isEqualTo(123);
    assertThat(result.getType().getLabel()).isEqualTo("Water");
  }

  @Test
  void toEntity_shouldHandleNullValue() {
    WeaknessDTO dto = WeaknessDTOMock.createMockWeaknessDTO("Grass", null);
    Type weaknessType = TypeMock.createMockType("Grass");

    Weakness result = mapper.toEntity(dto, weaknessType);

    assertThat(result).isNotNull();
    assertThat(result.getValue()).isNull();
    assertThat(result.getType()).isEqualTo(weaknessType);
  }

  @Test
  void toEntity_shouldHandleNullWeaknessType() {
    WeaknessDTO dto = WeaknessDTOMock.createMockWeaknessDTO("Lightning", "×2");

    Weakness result = mapper.toEntity(dto, null);

    assertThat(result).isNotNull();
    assertThat(result.getValue()).isEqualTo("×2");
    assertThat(result.getType()).isNull();
  }

  @Test
  void toEntity_shouldHandleNullDTO() {
    Type weaknessType = TypeMock.createMockType("Psychic");

    Exception exception = org.junit.jupiter.api.Assertions.assertThrows(
        NullPointerException.class,
        () -> mapper.toEntity(null, weaknessType));

    assertThat(exception).isNotNull();
  }
}
