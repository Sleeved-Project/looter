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
import com.sleeved.looter.domain.repository.atlas.LegalitiesRepository;
import com.sleeved.looter.mock.domain.LegalitiesMock;

@ExtendWith(MockitoExtension.class)
class LegalitiesServiceTest {

  @Mock
  private LegalitiesRepository legalitiesRepository;

  @InjectMocks
  private LegalitiesService legalitiesService;

  @Test
  void getOrCreate_shouldReturnExistingLegalities_whenLegalitiesExists() {
    Legalities inputLegalities = LegalitiesMock.createMockLegalities("Legal", "Legal", "Legal");
    Legalities existingLegalities = LegalitiesMock.createMockLegalitiesSavedInDb(1, "Legal", "Legal", "Legal");

    when(legalitiesRepository.findByStandardAndExpandedAndUnlimited("Legal", "Legal", "Legal"))
        .thenReturn(Optional.of(existingLegalities));

    Legalities result = legalitiesService.getOrCreate(inputLegalities);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1);
    assertThat(result.getStandard()).isEqualTo("Legal");
    assertThat(result.getExpanded()).isEqualTo("Legal");
    assertThat(result.getUnlimited()).isEqualTo("Legal");
    verify(legalitiesRepository).findByStandardAndExpandedAndUnlimited("Legal", "Legal", "Legal");
    verify(legalitiesRepository, never()).save(any(Legalities.class));
  }

  @Test
  void getOrCreate_shouldCreateAndReturnNewLegalities_whenLegalitiesDoesNotExist() {
    Legalities inputLegalities = LegalitiesMock.createMockLegalities("Legal", "Not Legal", "Legal");
    Legalities savedLegalities = LegalitiesMock.createMockLegalitiesSavedInDb(2, "Legal", "Not Legal", "Legal");

    when(legalitiesRepository.findByStandardAndExpandedAndUnlimited("Legal", "Not Legal", "Legal"))
        .thenReturn(Optional.empty());
    when(legalitiesRepository.save(inputLegalities)).thenReturn(savedLegalities);

    Legalities result = legalitiesService.getOrCreate(inputLegalities);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(2);
    assertThat(result.getStandard()).isEqualTo("Legal");
    assertThat(result.getExpanded()).isEqualTo("Not Legal");
    assertThat(result.getUnlimited()).isEqualTo("Legal");
    verify(legalitiesRepository).findByStandardAndExpandedAndUnlimited("Legal", "Not Legal", "Legal");
    verify(legalitiesRepository).save(inputLegalities);
  }

  @Test
  void getOrCreate_shouldHandleNullValues() {
    Legalities inputLegalities = LegalitiesMock.createMockLegalities(null, null, "Legal");
    Legalities savedLegalities = LegalitiesMock.createMockLegalitiesSavedInDb(3, null, null, "Legal");

    when(legalitiesRepository.findByStandardAndExpandedAndUnlimited(null, null, "Legal"))
        .thenReturn(Optional.empty());
    when(legalitiesRepository.save(inputLegalities)).thenReturn(savedLegalities);

    Legalities result = legalitiesService.getOrCreate(inputLegalities);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(3);
    assertThat(result.getStandard()).isNull();
    assertThat(result.getExpanded()).isNull();
    assertThat(result.getUnlimited()).isEqualTo("Legal");
    verify(legalitiesRepository).findByStandardAndExpandedAndUnlimited(null, null, "Legal");
    verify(legalitiesRepository).save(inputLegalities);
  }

  @Test
  void getOrCreate_shouldHandleAllNullValues() {
    Legalities inputLegalities = LegalitiesMock.createMockLegalities(null, null, null);
    Legalities savedLegalities = LegalitiesMock.createMockLegalitiesSavedInDb(4, null, null, null);

    when(legalitiesRepository.findByStandardAndExpandedAndUnlimited(null, null, null))
        .thenReturn(Optional.empty());
    when(legalitiesRepository.save(inputLegalities)).thenReturn(savedLegalities);

    Legalities result = legalitiesService.getOrCreate(inputLegalities);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(4);
    assertThat(result.getStandard()).isNull();
    assertThat(result.getExpanded()).isNull();
    assertThat(result.getUnlimited()).isNull();
    verify(legalitiesRepository).findByStandardAndExpandedAndUnlimited(null, null, null);
    verify(legalitiesRepository).save(inputLegalities);
  }

  @Test
  void getByStandardExpandedUnlimited_shouldReturnLegalities_whenLegalitiesExists() {
    Legalities inputLegalities = LegalitiesMock.createMockLegalities("Legal", "Legal", "Legal");
    Legalities existingLegalities = LegalitiesMock.createMockLegalitiesSavedInDb(5, "Legal", "Legal", "Legal");

    when(legalitiesRepository.findByStandardAndExpandedAndUnlimited("Legal", "Legal", "Legal"))
        .thenReturn(Optional.of(existingLegalities));

    Legalities result = legalitiesService.getByStandardExpandedUnlimited(inputLegalities);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(5);
    assertThat(result.getStandard()).isEqualTo("Legal");
    assertThat(result.getExpanded()).isEqualTo("Legal");
    assertThat(result.getUnlimited()).isEqualTo("Legal");
    verify(legalitiesRepository).findByStandardAndExpandedAndUnlimited("Legal", "Legal", "Legal");
  }

  @Test
  void getByStandardExpandedUnlimited_shouldThrowException_whenLegalitiesDoesNotExist() {
    Legalities inputLegalities = LegalitiesMock.createMockLegalities("Not Legal", "Not Legal", "Not Legal");

    when(legalitiesRepository.findByStandardAndExpandedAndUnlimited("Not Legal", "Not Legal", "Not Legal"))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> legalitiesService.getByStandardExpandedUnlimited(inputLegalities))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining(
            "Legalities not found for standard: Not Legal, expanded: Not Legal, unlimited: Not Legal");

    verify(legalitiesRepository).findByStandardAndExpandedAndUnlimited("Not Legal", "Not Legal", "Not Legal");
  }

  @Test
  void getByStandardExpandedUnlimited_shouldHandleNullValues() {
    Legalities inputLegalities = LegalitiesMock.createMockLegalities(null, "Legal", null);

    when(legalitiesRepository.findByStandardAndExpandedAndUnlimited(null, "Legal", null))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> legalitiesService.getByStandardExpandedUnlimited(inputLegalities))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Legalities not found for standard: null, expanded: Legal, unlimited: null");

    verify(legalitiesRepository).findByStandardAndExpandedAndUnlimited(null, "Legal", null);
  }

  @Test
  void getByStandardExpandedUnlimited_shouldHandleAllNullValues() {
    Legalities inputLegalities = LegalitiesMock.createMockLegalities(null, null, null);

    when(legalitiesRepository.findByStandardAndExpandedAndUnlimited(null, null, null))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> legalitiesService.getByStandardExpandedUnlimited(inputLegalities))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Legalities not found for standard: null, expanded: null, unlimited: null");

    verify(legalitiesRepository).findByStandardAndExpandedAndUnlimited(null, null, null);
  }
}
