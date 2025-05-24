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
import com.sleeved.looter.domain.repository.atlas.TypeRepository;
import com.sleeved.looter.mock.domain.TypeMock;

@ExtendWith(MockitoExtension.class)
class TypeServiceTest {

  @Mock
  private TypeRepository typeRepository;

  @InjectMocks
  private TypeService typeService;

  @Test
  void getOrCreate_shouldReturnExistingType_whenTypeExists() {
    Type inputType = TypeMock.createMockType("Pokémon");
    Type existingType = TypeMock.createMockTypeSavedInDb(1, "Pokémon");

    when(typeRepository.findByLabel("Pokémon")).thenReturn(Optional.of(existingType));

    Type result = typeService.getOrCreate(inputType);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1);
    assertThat(result.getLabel()).isEqualTo("Pokémon");
    verify(typeRepository).findByLabel("Pokémon");
    verify(typeRepository, never()).save(any(Type.class));
  }

  @Test
  void getOrCreate_shouldCreateAndReturnNewType_whenTypeDoesNotExist() {
    Type inputType = TypeMock.createMockType("Trainer");
    Type savedType = TypeMock.createMockTypeSavedInDb(2, "Trainer");

    when(typeRepository.findByLabel("Trainer")).thenReturn(Optional.empty());
    when(typeRepository.save(inputType)).thenReturn(savedType);

    Type result = typeService.getOrCreate(inputType);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(2);
    assertThat(result.getLabel()).isEqualTo("Trainer");
    verify(typeRepository).findByLabel("Trainer");
    verify(typeRepository).save(inputType);
  }

  @Test
  void getOrCreate_shouldHandleNullLabel() {
    Type inputType = TypeMock.createMockType(null);
    Type savedType = TypeMock.createMockTypeSavedInDb(3, null);

    when(typeRepository.findByLabel(null)).thenReturn(Optional.empty());
    when(typeRepository.save(inputType)).thenReturn(savedType);

    Type result = typeService.getOrCreate(inputType);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(3);
    assertThat(result.getLabel()).isNull();
    verify(typeRepository).findByLabel(null);
    verify(typeRepository).save(inputType);
  }
}