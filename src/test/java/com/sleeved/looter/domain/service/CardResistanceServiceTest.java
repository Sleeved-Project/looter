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

import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.entity.atlas.CardResistance;
import com.sleeved.looter.domain.entity.atlas.Resistance;
import com.sleeved.looter.domain.entity.atlas.Type;
import com.sleeved.looter.domain.repository.atlas.CardResistanceRepository;
import com.sleeved.looter.mock.domain.CardMock;
import com.sleeved.looter.mock.domain.CardResistanceMock;
import com.sleeved.looter.mock.domain.ResistanceMock;
import com.sleeved.looter.mock.domain.TypeMock;

@ExtendWith(MockitoExtension.class)
class CardResistanceServiceTest {

  @Mock
  private CardResistanceRepository cardResistanceRepository;

  @InjectMocks
  private CardResistanceService cardResistanceService;

  @Test
  void getOrCreate_shouldReturnExistingCardResistance_whenCardResistanceExists() {
    Type type = TypeMock.createMockTypeSavedInDb(1, "Fighting");
    Resistance resistance = ResistanceMock.createMockResistanceSavedInDb(1, type, "-30");
    Card card = CardMock.createBasicMockCard("swsh1-25", "Pikachu");

    CardResistance inputCardResistance = CardResistanceMock.createMockCardResistance(resistance, card);
    CardResistance existingCardResistance = CardResistanceMock.createMockCardResistanceSavedInDb(1, resistance, card);

    when(cardResistanceRepository.findByResistanceAndCard(resistance, card))
        .thenReturn(Optional.of(existingCardResistance));

    CardResistance result = cardResistanceService.getOrCreate(inputCardResistance);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1);
    assertThat(result.getResistance()).isEqualTo(resistance);
    assertThat(result.getCard()).isEqualTo(card);
    assertThat(result.getResistance().getType().getLabel()).isEqualTo("Fighting");
    assertThat(result.getResistance().getValue()).isEqualTo("-30");
    assertThat(result.getCard().getName()).isEqualTo("Pikachu");

    verify(cardResistanceRepository).findByResistanceAndCard(resistance, card);
    verify(cardResistanceRepository, never()).save(any(CardResistance.class));
  }

  @Test
  void getOrCreate_shouldCreateAndReturnNewCardResistance_whenCardResistanceDoesNotExist() {
    Type type = TypeMock.createMockTypeSavedInDb(2, "Psychic");
    Resistance resistance = ResistanceMock.createMockResistanceSavedInDb(2, type, "-20");
    Card card = CardMock.createBasicMockCard("swsh2-30", "Charizard");

    CardResistance inputCardResistance = CardResistanceMock.createMockCardResistance(resistance, card);
    CardResistance savedCardResistance = CardResistanceMock.createMockCardResistanceSavedInDb(2, resistance, card);

    when(cardResistanceRepository.findByResistanceAndCard(resistance, card))
        .thenReturn(Optional.empty());
    when(cardResistanceRepository.save(inputCardResistance)).thenReturn(savedCardResistance);

    CardResistance result = cardResistanceService.getOrCreate(inputCardResistance);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(2);
    assertThat(result.getResistance()).isEqualTo(resistance);
    assertThat(result.getCard()).isEqualTo(card);
    assertThat(result.getResistance().getType().getLabel()).isEqualTo("Psychic");
    assertThat(result.getResistance().getValue()).isEqualTo("-20");
    assertThat(result.getCard().getName()).isEqualTo("Charizard");

    verify(cardResistanceRepository).findByResistanceAndCard(resistance, card);
    verify(cardResistanceRepository).save(inputCardResistance);
  }

  @Test
  void getOrCreate_shouldHandleNullResistance() {
    Card card = CardMock.createBasicMockCard("swsh3-45", "Mewtwo");

    CardResistance inputCardResistance = CardResistanceMock.createMockCardResistance(null, card);
    CardResistance savedCardResistance = CardResistanceMock.createMockCardResistanceSavedInDb(3, null, card);

    when(cardResistanceRepository.findByResistanceAndCard(null, card))
        .thenReturn(Optional.empty());
    when(cardResistanceRepository.save(inputCardResistance)).thenReturn(savedCardResistance);

    CardResistance result = cardResistanceService.getOrCreate(inputCardResistance);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(3);
    assertThat(result.getResistance()).isNull();
    assertThat(result.getCard()).isEqualTo(card);
    assertThat(result.getCard().getName()).isEqualTo("Mewtwo");

    verify(cardResistanceRepository).findByResistanceAndCard(null, card);
    verify(cardResistanceRepository).save(inputCardResistance);
  }

  @Test
  void getOrCreate_shouldHandleNullCard() {
    Type type = TypeMock.createMockTypeSavedInDb(3, "Fire");
    Resistance resistance = ResistanceMock.createMockResistanceSavedInDb(3, type, "-30");

    CardResistance inputCardResistance = CardResistanceMock.createMockCardResistance(resistance, null);
    CardResistance savedCardResistance = CardResistanceMock.createMockCardResistanceSavedInDb(4, resistance, null);

    when(cardResistanceRepository.findByResistanceAndCard(resistance, null))
        .thenReturn(Optional.empty());
    when(cardResistanceRepository.save(inputCardResistance)).thenReturn(savedCardResistance);

    CardResistance result = cardResistanceService.getOrCreate(inputCardResistance);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(4);
    assertThat(result.getResistance()).isEqualTo(resistance);
    assertThat(result.getResistance().getType().getLabel()).isEqualTo("Fire");
    assertThat(result.getResistance().getValue()).isEqualTo("-30");
    assertThat(result.getCard()).isNull();

    verify(cardResistanceRepository).findByResistanceAndCard(resistance, null);
    verify(cardResistanceRepository).save(inputCardResistance);
  }

  @Test
  void getOrCreate_shouldHandleBothInputsNull() {
    CardResistance inputCardResistance = CardResistanceMock.createMockCardResistance(null, null);
    CardResistance savedCardResistance = CardResistanceMock.createMockCardResistanceSavedInDb(5, null, null);

    when(cardResistanceRepository.findByResistanceAndCard(null, null))
        .thenReturn(Optional.empty());
    when(cardResistanceRepository.save(inputCardResistance)).thenReturn(savedCardResistance);

    CardResistance result = cardResistanceService.getOrCreate(inputCardResistance);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(5);
    assertThat(result.getResistance()).isNull();
    assertThat(result.getCard()).isNull();

    verify(cardResistanceRepository).findByResistanceAndCard(null, null);
    verify(cardResistanceRepository).save(inputCardResistance);
  }
}