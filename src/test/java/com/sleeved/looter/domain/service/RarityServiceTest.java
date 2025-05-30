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

import com.sleeved.looter.domain.entity.atlas.Rarity;
import com.sleeved.looter.domain.repository.atlas.RarityRepository;
import com.sleeved.looter.mock.domain.RarityMock;

@ExtendWith(MockitoExtension.class)
class RarityServiceTest {

  @Mock
  private RarityRepository rarityRepository;

  @InjectMocks
  private RarityService rarityService;

  @Test
  void getOrCreate_shouldReturnExistingRarity_whenRarityExists() {
    Rarity inputRarity = RarityMock.createMockRarity("Common");
    Rarity existingRarity = RarityMock.createMockRaritySavedInDb(1, "Common");

    when(rarityRepository.findByLabel("Common")).thenReturn(Optional.of(existingRarity));

    Rarity result = rarityService.getOrCreate(inputRarity);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1);
    assertThat(result.getLabel()).isEqualTo("Common");
    verify(rarityRepository).findByLabel("Common");
    verify(rarityRepository, never()).save(any(Rarity.class));
  }

  @Test
  void getOrCreate_shouldCreateAndReturnNewRarity_whenRarityDoesNotExist() {
    Rarity inputRarity = RarityMock.createMockRarity("Ultra Rare");
    Rarity savedRarity = RarityMock.createMockRaritySavedInDb(2, "Ultra Rare");

    when(rarityRepository.findByLabel("Ultra Rare")).thenReturn(Optional.empty());
    when(rarityRepository.save(inputRarity)).thenReturn(savedRarity);

    Rarity result = rarityService.getOrCreate(inputRarity);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(2);
    assertThat(result.getLabel()).isEqualTo("Ultra Rare");
    verify(rarityRepository).findByLabel("Ultra Rare");
    verify(rarityRepository).save(inputRarity);
  }

  @Test
  void getOrCreate_shouldHandleNullLabel() {
    Rarity inputRarity = RarityMock.createMockRarity(null);
    Rarity savedRarity = RarityMock.createMockRaritySavedInDb(3, null);

    when(rarityRepository.findByLabel(null)).thenReturn(Optional.empty());
    when(rarityRepository.save(inputRarity)).thenReturn(savedRarity);

    Rarity result = rarityService.getOrCreate(inputRarity);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(3);
    assertThat(result.getLabel()).isNull();
    verify(rarityRepository).findByLabel(null);
    verify(rarityRepository).save(inputRarity);
  }

  @Test
  void getByLable_shouldReturnRarity_whenRarityExists() {
    String rarityLabel = "Rare Holo";
    Rarity existingRarity = RarityMock.createMockRaritySavedInDb(4, rarityLabel);

    when(rarityRepository.findByLabel(rarityLabel)).thenReturn(Optional.of(existingRarity));

    Rarity result = rarityService.getByLable(rarityLabel);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(4);
    assertThat(result.getLabel()).isEqualTo(rarityLabel);
    verify(rarityRepository).findByLabel(rarityLabel);
  }

  @Test
  void getByLable_shouldThrowException_whenRarityDoesNotExist() {
    String rarityLabel = "Nonexistent";
    when(rarityRepository.findByLabel(rarityLabel)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> rarityService.getByLable(rarityLabel))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("Rarity not found for label: Nonexistent");

    verify(rarityRepository).findByLabel(rarityLabel);
  }
}
