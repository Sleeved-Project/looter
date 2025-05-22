package com.sleeved.looter.infra.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sleeved.looter.domain.entity.atlas.Artist;

@ExtendWith(MockitoExtension.class)
public class ArtistMapperTest {

  @InjectMocks
  private ArtistMapper mapper;

  @Test
  void toEntity_shouldMapValidName() {
    String artistName = "John Avon";

    Artist result = mapper.toEntity(artistName);

    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo("John Avon");
  }

  @Test
  void toEntity_shouldTrimName() {
    String artistName = "  Rebecca Guay  ";

    Artist result = mapper.toEntity(artistName);

    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo("Rebecca Guay");
  }

  @Test
  void toEntity_shouldHandleNullName() {
    Artist result = mapper.toEntity(null);

    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo("UNKNOWN");
  }

  @Test
  void toEntity_shouldHandleEmptyName() {
    String artistName = "";

    Artist result = mapper.toEntity(artistName);

    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo("UNKNOWN");
  }
}
