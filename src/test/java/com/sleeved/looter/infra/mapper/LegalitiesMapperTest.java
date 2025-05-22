package com.sleeved.looter.infra.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sleeved.looter.domain.entity.atlas.Legalities;
import com.sleeved.looter.infra.dto.LegalitiesDTO;
import com.sleeved.looter.mock.LegalitiesDTOMock;

@ExtendWith(MockitoExtension.class)
public class LegalitiesMapperTest {

  @InjectMocks
  private LegalitiesMapper mapper;

  @Test
  void toEntity_shouldMapValidDTO() {
    LegalitiesDTO dto = LegalitiesDTOMock.createMockLegalitiesDTO("Legal", "Legal", "Legal");

    Legalities result = mapper.toEntity(dto);

    assertThat(result).isNotNull();
    assertThat(result.getStandard()).isEqualTo("Legal");
    assertThat(result.getExpanded()).isEqualTo("Legal");
    assertThat(result.getUnlimited()).isEqualTo("Legal");
  }

  @Test
  void toEntity_shouldHandleNullDTO() {
    Legalities result = mapper.toEntity(null);

    assertThat(result).isNotNull();
    assertThat(result.getStandard()).isNull();
    assertThat(result.getExpanded()).isNull();
    assertThat(result.getUnlimited()).isNull();
  }

  @Test
  void toEntity_shouldHandleDTOWithNullValues() {
    LegalitiesDTO dto = LegalitiesDTOMock.createMockLegalitiesDTO(null, null, null);

    Legalities result = mapper.toEntity(dto);

    assertThat(result).isNotNull();
    assertThat(result.getStandard()).isNull();
    assertThat(result.getExpanded()).isNull();
    assertThat(result.getUnlimited()).isNull();
  }

  @Test
  void toEntity_shouldMapDTOWithPartialValues() {
    LegalitiesDTO dto = LegalitiesDTOMock.createMockLegalitiesDTO("Legal", null, "Not Legal");

    Legalities result = mapper.toEntity(dto);

    assertThat(result).isNotNull();
    assertThat(result.getStandard()).isEqualTo("Legal");
    assertThat(result.getExpanded()).isNull();
    assertThat(result.getUnlimited()).isEqualTo("Not Legal");
  }

  @Test
  void toEntity_shouldHandleDifferentLegalityValues() {
    LegalitiesDTO dto = LegalitiesDTOMock.createMockLegalitiesDTO("Legal", "Not Legal", "Banned");

    Legalities result = mapper.toEntity(dto);

    assertThat(result).isNotNull();
    assertThat(result.getStandard()).isEqualTo("Legal");
    assertThat(result.getExpanded()).isEqualTo("Not Legal");
    assertThat(result.getUnlimited()).isEqualTo("Banned");
  }
}
