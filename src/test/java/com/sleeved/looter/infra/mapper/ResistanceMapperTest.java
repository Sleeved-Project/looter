package com.sleeved.looter.infra.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sleeved.looter.domain.entity.atlas.Resistance;
import com.sleeved.looter.domain.entity.atlas.Type;
import com.sleeved.looter.infra.dto.ResistanceDTO;
import com.sleeved.looter.mock.domain.TypeMock;
import com.sleeved.looter.mock.infra.ResistanceDTOMock;

@ExtendWith(MockitoExtension.class)
public class ResistanceMapperTest {

  @InjectMocks
  private ResistanceMapper mapper;

  @Test
  void toEntity_shouldMapValidDTO() {
    ResistanceDTO dto = ResistanceDTOMock.createMockResistanceDTO("Fire", "-30");
    Type resistanceType = TypeMock.createMockType("Fire");

    Resistance result = mapper.toEntity(dto, resistanceType);

    assertThat(result).isNotNull();
    assertThat(result.getValue()).isEqualTo("-30");
    assertThat(result.getType()).isEqualTo(resistanceType);
    assertThat(result.getType().getLabel()).isEqualTo("Fire");
  }

  @Test
  void toEntity_shouldHandleTypeWithId() {
    ResistanceDTO dto = ResistanceDTOMock.createMockResistanceDTO("Water", "-20");
    Type resistanceType = TypeMock.createMockTypeSavedInDb(123, "Water");

    Resistance result = mapper.toEntity(dto, resistanceType);

    assertThat(result).isNotNull();
    assertThat(result.getValue()).isEqualTo("-20");
    assertThat(result.getType()).isEqualTo(resistanceType);
    assertThat(result.getType().getId()).isEqualTo(123);
    assertThat(result.getType().getLabel()).isEqualTo("Water");
  }

  @Test
  void toEntity_shouldHandleNullValue() {
    ResistanceDTO dto = ResistanceDTOMock.createMockResistanceDTO("Grass", null);
    Type resistanceType = TypeMock.createMockType("Grass");

    Resistance result = mapper.toEntity(dto, resistanceType);

    assertThat(result).isNotNull();
    assertThat(result.getValue()).isNull();
    assertThat(result.getType()).isEqualTo(resistanceType);
  }

  @Test
  void toEntity_shouldHandleNullResistanceType() {
    ResistanceDTO dto = ResistanceDTOMock.createMockResistanceDTO("Lightning", "-20");

    Resistance result = mapper.toEntity(dto, null);

    assertThat(result).isNotNull();
    assertThat(result.getValue()).isEqualTo("-20");
    assertThat(result.getType()).isNull();
  }

  @Test
  void toEntity_shouldHandleNullDTO() {
    Type resistanceType = TypeMock.createMockType("Psychic");

    Exception exception = org.junit.jupiter.api.Assertions.assertThrows(
        NullPointerException.class,
        () -> mapper.toEntity(null, resistanceType));

    assertThat(exception).isNotNull();
  }
}