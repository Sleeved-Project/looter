package com.sleeved.looter.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sleeved.looter.domain.entity.atlas.Artist;
import com.sleeved.looter.domain.repository.atlas.ArtistRepository;
import com.sleeved.looter.mock.domain.ArtistMock;

@ExtendWith(MockitoExtension.class)
class ArtistServiceTest {

  @Mock
  private ArtistRepository artistRepository;

  @InjectMocks
  private ArtistService artistService;

  @Test
  void getOrCreate_shouldReturnExistingArtist_whenArtistExists() {
    Artist inputArtist = ArtistMock.createMockArtist("Ken Sugimori");
    Artist existingArtist = ArtistMock.createMockArtistSavedInDb(1, "Ken Sugimori");

    when(artistRepository.findByName("Ken Sugimori")).thenReturn(Optional.of(existingArtist));

    Artist result = artistService.getOrCreate(inputArtist);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1);
    assertThat(result.getName()).isEqualTo("Ken Sugimori");
    verify(artistRepository).findByName("Ken Sugimori");
    verify(artistRepository, never()).save(any(Artist.class));
  }

  @Test
  void getOrCreate_shouldCreateAndReturnNewArtist_whenArtistDoesNotExist() {
    Artist inputArtist = ArtistMock.createMockArtist("Mitsuhiro Arita");
    Artist savedArtist = ArtistMock.createMockArtistSavedInDb(2, "Mitsuhiro Arita");

    when(artistRepository.findByName("Mitsuhiro Arita")).thenReturn(Optional.empty());
    when(artistRepository.save(inputArtist)).thenReturn(savedArtist);

    Artist result = artistService.getOrCreate(inputArtist);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(2);
    assertThat(result.getName()).isEqualTo("Mitsuhiro Arita");
    verify(artistRepository).findByName("Mitsuhiro Arita");
    verify(artistRepository).save(inputArtist);
  }

  @Test
  void getOrCreate_shouldHandleNullName() {
    Artist inputArtist = ArtistMock.createMockArtist(null);
    Artist savedArtist = ArtistMock.createMockArtistSavedInDb(3, null);

    when(artistRepository.findByName(null)).thenReturn(Optional.empty());
    when(artistRepository.save(inputArtist)).thenReturn(savedArtist);

    Artist result = artistService.getOrCreate(inputArtist);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(3);
    assertThat(result.getName()).isNull();
    verify(artistRepository).findByName(null);
    verify(artistRepository).save(inputArtist);
  }

  @Test
  void getByName_shouldReturnArtist_whenArtistExists() {
    String artistName = "Tomokazu Komiya";
    Artist existingArtist = ArtistMock.createMockArtistSavedInDb(4, artistName);

    when(artistRepository.findByName(artistName)).thenReturn(Optional.of(existingArtist));

    Artist result = artistService.getByName(artistName);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(4);
    assertThat(result.getName()).isEqualTo(artistName);
    verify(artistRepository).findByName(artistName);
  }

  @Test
  void getByName_shouldThrowException_whenArtistDoesNotExist() {
    String artistName = "Nonexistent Artist";
    when(artistRepository.findByName(artistName)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> artistService.getByName(artistName))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("Artist not found for name: Nonexistent Artist");

    verify(artistRepository).findByName(artistName);
  }
}
