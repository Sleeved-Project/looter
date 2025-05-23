package com.sleeved.looter.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
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
    // Préparation
    Artist inputArtist = ArtistMock.createMockArtist("Ken Sugimori");
    Artist existingArtist = ArtistMock.createMockArtistSavedInDb(1, "Ken Sugimori");

    when(artistRepository.findByName("Ken Sugimori")).thenReturn(Optional.of(existingArtist));

    // Exécution
    Artist result = artistService.getOrCreate(inputArtist);

    // Vérification
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1);
    assertThat(result.getName()).isEqualTo("Ken Sugimori");
    verify(artistRepository).findByName("Ken Sugimori");
    verify(artistRepository, never()).save(any(Artist.class));
  }

  @Test
  void getOrCreate_shouldCreateAndReturnNewArtist_whenArtistDoesNotExist() {
    // Préparation
    Artist inputArtist = ArtistMock.createMockArtist("Mitsuhiro Arita");
    Artist savedArtist = ArtistMock.createMockArtistSavedInDb(2, "Mitsuhiro Arita");

    when(artistRepository.findByName("Mitsuhiro Arita")).thenReturn(Optional.empty());
    when(artistRepository.save(inputArtist)).thenReturn(savedArtist);

    // Exécution
    Artist result = artistService.getOrCreate(inputArtist);

    // Vérification
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(2);
    assertThat(result.getName()).isEqualTo("Mitsuhiro Arita");
    verify(artistRepository).findByName("Mitsuhiro Arita");
    verify(artistRepository).save(inputArtist);
  }

  @Test
  void getOrCreate_shouldHandleNullName() {
    // Préparation
    Artist inputArtist = ArtistMock.createMockArtist(null);
    Artist savedArtist = ArtistMock.createMockArtistSavedInDb(3, null);

    when(artistRepository.findByName(null)).thenReturn(Optional.empty());
    when(artistRepository.save(inputArtist)).thenReturn(savedArtist);

    // Exécution
    Artist result = artistService.getOrCreate(inputArtist);

    // Vérification
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(3);
    assertThat(result.getName()).isNull();
    verify(artistRepository).findByName(null);
    verify(artistRepository).save(inputArtist);
  }
}
