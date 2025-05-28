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

import com.sleeved.looter.domain.entity.atlas.Type;
import com.sleeved.looter.domain.entity.atlas.Weakness;
import com.sleeved.looter.domain.repository.atlas.WeaknessRepository;
import com.sleeved.looter.mock.domain.TypeMock;
import com.sleeved.looter.mock.domain.WeaknessMock;

@ExtendWith(MockitoExtension.class)
class WeaknessServiceTest {

  @Mock
  private WeaknessRepository weaknessRepository;

  @InjectMocks
  private WeaknessService weaknessService;

  @Test
  void getOrCreate_shouldReturnExistingWeakness_whenWeaknessExists() {
    Type type = TypeMock.createMockTypeSavedInDb(1, "Fire");
    Weakness inputWeakness = WeaknessMock.createMockWeakness(type, "×2");
    Weakness existingWeakness = WeaknessMock.createMockWeaknessSavedInDb(1, type, "×2");

    when(weaknessRepository.findByTypeAndValue(type, "×2")).thenReturn(Optional.of(existingWeakness));

    Weakness result = weaknessService.getOrCreate(inputWeakness);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1);
    assertThat(result.getValue()).isEqualTo("×2");
    assertThat(result.getType()).isEqualTo(type);
    assertThat(result.getType().getId()).isEqualTo(1);
    assertThat(result.getType().getLabel()).isEqualTo("Fire");
    verify(weaknessRepository).findByTypeAndValue(type, "×2");
    verify(weaknessRepository, never()).save(any(Weakness.class));
  }

  @Test
  void getOrCreate_shouldCreateAndReturnNewWeakness_whenWeaknessDoesNotExist() {
    Type type = TypeMock.createMockTypeSavedInDb(2, "Water");
    Weakness inputWeakness = WeaknessMock.createMockWeakness(type, "×2");
    Weakness savedWeakness = WeaknessMock.createMockWeaknessSavedInDb(2, type, "×2");

    when(weaknessRepository.findByTypeAndValue(type, "×2")).thenReturn(Optional.empty());
    when(weaknessRepository.save(inputWeakness)).thenReturn(savedWeakness);

    Weakness result = weaknessService.getOrCreate(inputWeakness);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(2);
    assertThat(result.getValue()).isEqualTo("×2");
    assertThat(result.getType()).isEqualTo(type);
    assertThat(result.getType().getId()).isEqualTo(2);
    assertThat(result.getType().getLabel()).isEqualTo("Water");
    verify(weaknessRepository).findByTypeAndValue(type, "×2");
    verify(weaknessRepository).save(inputWeakness);
  }

  @Test
  void getOrCreate_shouldHandleNullValue() {
    Type type = TypeMock.createMockTypeSavedInDb(4, "Grass");
    Weakness inputWeakness = WeaknessMock.createMockWeakness(type, null);
    Weakness savedWeakness = WeaknessMock.createMockWeaknessSavedInDb(4, type, null);

    when(weaknessRepository.findByTypeAndValue(type, null)).thenReturn(Optional.empty());
    when(weaknessRepository.save(inputWeakness)).thenReturn(savedWeakness);

    Weakness result = weaknessService.getOrCreate(inputWeakness);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(4);
    assertThat(result.getValue()).isNull();
    assertThat(result.getType().getLabel()).isEqualTo("Grass");
    verify(weaknessRepository).findByTypeAndValue(type, null);
    verify(weaknessRepository).save(inputWeakness);
  }

  @Test
  void getOrCreate_shouldHandleNullType() {
    Weakness inputWeakness = WeaknessMock.createMockWeakness(null, "×2");
    Weakness savedWeakness = WeaknessMock.createMockWeaknessSavedInDb(5, null, "×2");

    when(weaknessRepository.findByTypeAndValue(null, "×2")).thenReturn(Optional.empty());
    when(weaknessRepository.save(inputWeakness)).thenReturn(savedWeakness);

    Weakness result = weaknessService.getOrCreate(inputWeakness);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(5);
    assertThat(result.getValue()).isEqualTo("×2");
    assertThat(result.getType()).isNull();
    verify(weaknessRepository).findByTypeAndValue(null, "×2");
    verify(weaknessRepository).save(inputWeakness);
  }
}