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

import com.sleeved.looter.domain.entity.atlas.Subtype;
import com.sleeved.looter.domain.repository.atlas.SubtypeRepository;
import com.sleeved.looter.mock.domain.SubtypeMock;

@ExtendWith(MockitoExtension.class)
class SubtypeServiceTest {

  @Mock
  private SubtypeRepository subtypeRepository;

  @InjectMocks
  private SubtypeService subtypeService;

  @Test
  void getOrCreate_shouldReturnExistingSubtype_whenSubtypeExists() {
    Subtype inputSubtype = SubtypeMock.createMockSubtype("Basic");
    Subtype existingSubtype = SubtypeMock.createMockSubtypeSavedInDb(1, "Basic");

    when(subtypeRepository.findByLabel("Basic")).thenReturn(Optional.of(existingSubtype));

    Subtype result = subtypeService.getOrCreate(inputSubtype);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1);
    assertThat(result.getLabel()).isEqualTo("Basic");
    verify(subtypeRepository).findByLabel("Basic");
    verify(subtypeRepository, never()).save(any(Subtype.class));
  }

  @Test
  void getOrCreate_shouldCreateAndReturnNewSubtype_whenSubtypeDoesNotExist() {
    Subtype inputSubtype = SubtypeMock.createMockSubtype("Evolution");
    Subtype savedSubtype = SubtypeMock.createMockSubtypeSavedInDb(2, "Evolution");

    when(subtypeRepository.findByLabel("Evolution")).thenReturn(Optional.empty());
    when(subtypeRepository.save(inputSubtype)).thenReturn(savedSubtype);

    Subtype result = subtypeService.getOrCreate(inputSubtype);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(2);
    assertThat(result.getLabel()).isEqualTo("Evolution");
    verify(subtypeRepository).findByLabel("Evolution");
    verify(subtypeRepository).save(inputSubtype);
  }

  @Test
  void getOrCreate_shouldHandleNullLabel() {
    Subtype inputSubtype = SubtypeMock.createMockSubtype(null);
    Subtype savedSubtype = SubtypeMock.createMockSubtypeSavedInDb(3, null);

    when(subtypeRepository.findByLabel(null)).thenReturn(Optional.empty());
    when(subtypeRepository.save(inputSubtype)).thenReturn(savedSubtype);

    Subtype result = subtypeService.getOrCreate(inputSubtype);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(3);
    assertThat(result.getLabel()).isNull();
    verify(subtypeRepository).findByLabel(null);
    verify(subtypeRepository).save(inputSubtype);
  }

  @Test
  void getByLabel_shouldReturnSubtype_whenSubtypeExists() {
    String label = "Stage 1";
    Subtype existingSubtype = SubtypeMock.createMockSubtypeSavedInDb(4, label);

    when(subtypeRepository.findByLabel(label)).thenReturn(Optional.of(existingSubtype));

    Subtype result = subtypeService.getByLabel(label);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(4);
    assertThat(result.getLabel()).isEqualTo("Stage 1");

    verify(subtypeRepository).findByLabel(label);
  }

  @Test
  void getByLabel_shouldThrowException_whenSubtypeDoesNotExist() {
    String label = "NonExistentSubtype";

    when(subtypeRepository.findByLabel(label)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> subtypeService.getByLabel(label))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Subtype not found for label: " + label);

    verify(subtypeRepository).findByLabel(label);
  }

  @Test
  void getByLabel_shouldHandleNullLabel() {
    String label = null;
    Subtype existingSubtype = SubtypeMock.createMockSubtypeSavedInDb(5, null);

    when(subtypeRepository.findByLabel(null)).thenReturn(Optional.of(existingSubtype));

    Subtype result = subtypeService.getByLabel(label);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(5);
    assertThat(result.getLabel()).isNull();

    verify(subtypeRepository).findByLabel(null);
  }
}