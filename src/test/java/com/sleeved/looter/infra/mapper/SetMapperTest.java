package com.sleeved.looter.infra.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sleeved.looter.domain.entity.atlas.Legalities;
import com.sleeved.looter.domain.entity.atlas.Set;
import com.sleeved.looter.infra.dto.SetDTO;
import com.sleeved.looter.mock.domain.LegalitiesMock;
import com.sleeved.looter.mock.infra.SetDTOMock;

@ExtendWith(MockitoExtension.class)
public class SetMapperTest {

  @InjectMocks
  private SetMapper mapper;

  @Test
  void toEntity_shouldMapValidDTO() {
    SetDTO dto = SetDTOMock.createMockSetDTO("set123", "Base Set", "Series 1", 150, 151, "BASE",
        "2023/05/15", "2023/05/15 10:30:45", "symbol.png", "logo.png");

    Legalities legalities = LegalitiesMock.createMockLegalities("Legal", "Legal", "Legal");

    Set result = mapper.toEntity(dto, legalities);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo("set123");
    assertThat(result.getName()).isEqualTo("Base Set");
    assertThat(result.getSeries()).isEqualTo("Series 1");
    assertThat(result.getPrintedTotal()).isEqualTo(150);
    assertThat(result.getTotal()).isEqualTo(151);
    assertThat(result.getPtcgoCode()).isEqualTo("BASE");
    assertThat(result.getReleaseDate()).isEqualTo(LocalDate.of(2023, 5, 15));
    assertThat(result.getUpdatedAt()).isEqualTo(LocalDateTime.of(2023, 5, 15, 10, 30, 45));
    assertThat(result.getImageSymbol()).isEqualTo("symbol.png");
    assertThat(result.getImageLogo()).isEqualTo("logo.png");
    assertThat(result.getLegalities()).isEqualTo(legalities);
  }

  @Test
  void toEntity_shouldHandleNullDates() {
    SetDTO dto = SetDTOMock.createMockSetDTO("set123", "Base Set", "Series 1", 150, 151, "BASE",
        null, null, "symbol.png", "logo.png");

    Legalities legalities = LegalitiesMock.createMockLegalities("Legal", "Legal", "Legal");

    Set result = mapper.toEntity(dto, legalities);

    assertThat(result).isNotNull();
    assertThat(result.getReleaseDate()).isNull();
    assertThat(result.getUpdatedAt()).isNull();
  }

  @Test
  void toEntity_shouldHandleEmptyDates() {
    SetDTO dto = SetDTOMock.createMockSetDTO("set123", "Base Set", "Series 1", 150, 151, "BASE",
        "", "", "symbol.png", "logo.png");

    Legalities legalities = LegalitiesMock.createMockLegalities("Legal", "Legal", "Legal");

    Set result = mapper.toEntity(dto, legalities);

    assertThat(result).isNotNull();
    assertThat(result.getReleaseDate()).isNull();
    assertThat(result.getUpdatedAt()).isNull();
  }

  @Test
  void toEntity_shouldHandleNullLegalities() {
    SetDTO dto = SetDTOMock.createMockSetDTO("set123", "Base Set", "Series 1", 150, 151, "BASE",
        "2023/05/15", "2023/05/15 10:30:45", "symbol.png", "logo.png");

    Set result = mapper.toEntity(dto, null);

    assertThat(result).isNotNull();
    assertThat(result.getLegalities()).isNull();
  }
}