package com.sleeved.looter.infra.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sleeved.looter.domain.entity.atlas.Rarity;

@ExtendWith(MockitoExtension.class)
public class RarityMapperTest {

  @InjectMocks
  private RarityMapper mapper;

  @Test
  void toEntity_shouldMapValidLabel() {
    String rarityLabel = "Rare";

    Rarity result = mapper.toEntity(rarityLabel);

    assertThat(result).isNotNull();
    assertThat(result.getLabel()).isEqualTo("Rare");
  }

  @Test
  void toEntity_shouldTrimLabel() {
    String rarityLabel = "  Mythic  ";

    Rarity result = mapper.toEntity(rarityLabel);

    assertThat(result).isNotNull();
    assertThat(result.getLabel()).isEqualTo("Mythic");
  }

  @Test
  void toEntity_shouldHandleNullLabel() {
    Rarity result = mapper.toEntity(null);

    assertThat(result).isNotNull();
    assertThat(result.getLabel()).isEqualTo("UNKNOWN");
  }

  @Test
  void toEntity_shouldHandleEmptyLabel() {
    String rarityLabel = "";

    Rarity result = mapper.toEntity(rarityLabel);

    assertThat(result).isNotNull();
    assertThat(result.getLabel()).isEqualTo("UNKNOWN");
  }
}
