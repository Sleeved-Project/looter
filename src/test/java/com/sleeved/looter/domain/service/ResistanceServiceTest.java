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
import com.sleeved.looter.domain.entity.atlas.Resistance;
import com.sleeved.looter.domain.repository.atlas.ResistanceRepository;
import com.sleeved.looter.mock.domain.TypeMock;
import com.sleeved.looter.mock.domain.ResistanceMock;

@ExtendWith(MockitoExtension.class)
class ResistanceServiceTest {

  @Mock
  private ResistanceRepository resistanceRepository;

  @InjectMocks
  private ResistanceService resistanceService;

  @Test
  void getOrCreate_shouldReturnExistingResistance_whenResistanceExists() {
    Type type = TypeMock.createMockTypeSavedInDb(1, "Fire");
    Resistance inputResistance = ResistanceMock.createMockResistance(type, "-30");
    Resistance existingResistance = ResistanceMock.createMockResistanceSavedInDb(1, type, "-30");

    when(resistanceRepository.findByTypeAndValue(type, "-30")).thenReturn(Optional.of(existingResistance));

    Resistance result = resistanceService.getOrCreate(inputResistance);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1);
    assertThat(result.getValue()).isEqualTo("-30");
    assertThat(result.getType()).isEqualTo(type);
    assertThat(result.getType().getId()).isEqualTo(1);
    assertThat(result.getType().getLabel()).isEqualTo("Fire");
    verify(resistanceRepository).findByTypeAndValue(type, "-30");
    verify(resistanceRepository, never()).save(any(Resistance.class));
  }

  @Test
  void getOrCreate_shouldCreateAndReturnNewResistance_whenResistanceDoesNotExist() {
    Type type = TypeMock.createMockTypeSavedInDb(2, "Water");
    Resistance inputResistance = ResistanceMock.createMockResistance(type, "-20");
    Resistance savedResistance = ResistanceMock.createMockResistanceSavedInDb(2, type, "-20");

    when(resistanceRepository.findByTypeAndValue(type, "-20")).thenReturn(Optional.empty());
    when(resistanceRepository.save(inputResistance)).thenReturn(savedResistance);

    Resistance result = resistanceService.getOrCreate(inputResistance);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(2);
    assertThat(result.getValue()).isEqualTo("-20");
    assertThat(result.getType()).isEqualTo(type);
    assertThat(result.getType().getId()).isEqualTo(2);
    assertThat(result.getType().getLabel()).isEqualTo("Water");
    verify(resistanceRepository).findByTypeAndValue(type, "-20");
    verify(resistanceRepository).save(inputResistance);
  }

  @Test
  void getOrCreate_shouldHandleNullValue() {
    Type type = TypeMock.createMockTypeSavedInDb(4, "Grass");
    Resistance inputResistance = ResistanceMock.createMockResistance(type, null);
    Resistance savedResistance = ResistanceMock.createMockResistanceSavedInDb(4, type, null);

    when(resistanceRepository.findByTypeAndValue(type, null)).thenReturn(Optional.empty());
    when(resistanceRepository.save(inputResistance)).thenReturn(savedResistance);

    Resistance result = resistanceService.getOrCreate(inputResistance);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(4);
    assertThat(result.getValue()).isNull();
    assertThat(result.getType().getLabel()).isEqualTo("Grass");
    verify(resistanceRepository).findByTypeAndValue(type, null);
    verify(resistanceRepository).save(inputResistance);
  }

  @Test
  void getOrCreate_shouldHandleNullType() {
    Resistance inputResistance = ResistanceMock.createMockResistance(null, "-30");
    Resistance savedResistance = ResistanceMock.createMockResistanceSavedInDb(5, null, "-30");

    when(resistanceRepository.findByTypeAndValue(null, "-30")).thenReturn(Optional.empty());
    when(resistanceRepository.save(inputResistance)).thenReturn(savedResistance);

    Resistance result = resistanceService.getOrCreate(inputResistance);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(5);
    assertThat(result.getValue()).isEqualTo("-30");
    assertThat(result.getType()).isNull();
    verify(resistanceRepository).findByTypeAndValue(null, "-30");
    verify(resistanceRepository).save(inputResistance);
  }
}