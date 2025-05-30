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

import com.sleeved.looter.domain.entity.atlas.Legalities;
import com.sleeved.looter.domain.entity.atlas.Set;
import com.sleeved.looter.domain.repository.atlas.SetRepository;
import com.sleeved.looter.mock.domain.LegalitiesMock;
import com.sleeved.looter.mock.domain.SetMock;

@ExtendWith(MockitoExtension.class)
class SetServiceTest {

  @Mock
  private SetRepository setRepository;

  @InjectMocks
  private SetService setService;

  @Test
  void getOrCreate_shouldReturnExistingSet_whenSetExists() {
    Legalities legalities = LegalitiesMock.createMockLegalitiesSavedInDb(1, "Legal", "Legal", "Legal");
    Set inputSet = SetMock.createMockSet("base1", "Base Set", "Base", 102, 102, "BS",
        "symbol-base.png", "logo-base.png", legalities);
    Set existingSet = SetMock.createMockSet("base1", "Base Set", "Base", 102, 102, "BS",
        "symbol-base.png", "logo-base.png", legalities);

    when(setRepository.findById("base1")).thenReturn(Optional.of(existingSet));

    Set result = setService.getOrCreate(inputSet);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo("base1");
    assertThat(result.getName()).isEqualTo("Base Set");
    assertThat(result.getSeries()).isEqualTo("Base");
    assertThat(result.getPrintedTotal()).isEqualTo(102);
    assertThat(result.getTotal()).isEqualTo(102);
    assertThat(result.getPtcgoCode()).isEqualTo("BS");
    assertThat(result.getImageSymbol()).isEqualTo("symbol-base.png");
    assertThat(result.getImageLogo()).isEqualTo("logo-base.png");
    assertThat(result.getLegalities()).isEqualTo(legalities);

    verify(setRepository).findById("base1");
    verify(setRepository, never()).save(any(Set.class));
  }

  @Test
  void getOrCreate_shouldCreateAndReturnNewSet_whenSetDoesNotExist() {
    Legalities legalities = LegalitiesMock.createMockLegalitiesSavedInDb(2, "Legal", "Legal", "Legal");
    Set inputSet = SetMock.createMockSet("fossil", "Fossil", "Base", 62, 62, "FO",
        "symbol-fossil.png", "logo-fossil.png", legalities);
    Set savedSet = SetMock.createMockSet("fossil", "Fossil", "Base", 62, 62, "FO",
        "symbol-fossil.png", "logo-fossil.png", legalities);

    when(setRepository.findById("fossil")).thenReturn(Optional.empty());
    when(setRepository.save(inputSet)).thenReturn(savedSet);

    Set result = setService.getOrCreate(inputSet);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo("fossil");
    assertThat(result.getName()).isEqualTo("Fossil");
    assertThat(result.getSeries()).isEqualTo("Base");
    assertThat(result.getPrintedTotal()).isEqualTo(62);
    assertThat(result.getTotal()).isEqualTo(62);
    assertThat(result.getPtcgoCode()).isEqualTo("FO");
    assertThat(result.getImageSymbol()).isEqualTo("symbol-fossil.png");
    assertThat(result.getImageLogo()).isEqualTo("logo-fossil.png");
    assertThat(result.getLegalities()).isEqualTo(legalities);

    verify(setRepository).findById("fossil");
    verify(setRepository).save(inputSet);
  }

  @Test
  void getOrCreate_shouldHandleNullPtcgoCode() {
    Legalities legalities = LegalitiesMock.createMockLegalitiesSavedInDb(3, "Legal", "Legal", "Legal");
    Set inputSet = SetMock.createMockSet("sm1", "Sun & Moon", "Sun & Moon", 149, 149, null,
        "symbol-sm.png", "logo-sm.png", legalities);
    Set savedSet = SetMock.createMockSet("sm1", "Sun & Moon", "Sun & Moon", 149, 149, null,
        "symbol-sm.png", "logo-sm.png", legalities);

    when(setRepository.findById("sm1")).thenReturn(Optional.empty());
    when(setRepository.save(inputSet)).thenReturn(savedSet);

    Set result = setService.getOrCreate(inputSet);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo("sm1");
    assertThat(result.getName()).isEqualTo("Sun & Moon");
    assertThat(result.getSeries()).isEqualTo("Sun & Moon");
    assertThat(result.getPrintedTotal()).isEqualTo(149);
    assertThat(result.getTotal()).isEqualTo(149);
    assertThat(result.getPtcgoCode()).isNull();
    assertThat(result.getImageSymbol()).isEqualTo("symbol-sm.png");
    assertThat(result.getImageLogo()).isEqualTo("logo-sm.png");
    assertThat(result.getLegalities()).isEqualTo(legalities);

    verify(setRepository).findById("sm1");
    verify(setRepository).save(inputSet);
  }

  @Test
  void getById_shouldReturnSet_whenSetExists() {
    String setId = "swsh1";
    Legalities legalities = LegalitiesMock.createMockLegalitiesSavedInDb(4, "Legal", "Legal", "Legal");
    Set existingSet = SetMock.createMockSet(setId, "Sword & Shield", "Sword & Shield", 202, 202, "SSH",
        "symbol-swsh.png", "logo-swsh.png", legalities);

    when(setRepository.findById(setId)).thenReturn(Optional.of(existingSet));

    Set result = setService.getById(setId);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(setId);
    assertThat(result.getName()).isEqualTo("Sword & Shield");
    assertThat(result.getSeries()).isEqualTo("Sword & Shield");
    assertThat(result.getPrintedTotal()).isEqualTo(202);
    assertThat(result.getTotal()).isEqualTo(202);
    assertThat(result.getPtcgoCode()).isEqualTo("SSH");
    assertThat(result.getImageSymbol()).isEqualTo("symbol-swsh.png");
    assertThat(result.getImageLogo()).isEqualTo("logo-swsh.png");
    assertThat(result.getLegalities()).isEqualTo(legalities);

    verify(setRepository).findById(setId);
  }

  @Test
  void getById_shouldThrowException_whenSetDoesNotExist() {
    String setId = "nonexistent";
    when(setRepository.findById(setId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> setService.getById(setId))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("Set not found for id: nonexistent");

    verify(setRepository).findById(setId);
  }
}