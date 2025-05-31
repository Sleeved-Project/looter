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
import com.sleeved.looter.domain.entity.atlas.CardWeakness;
import com.sleeved.looter.domain.entity.atlas.Type;
import com.sleeved.looter.domain.entity.atlas.Weakness;
import com.sleeved.looter.domain.repository.atlas.CardWeaknessRepository;
import com.sleeved.looter.mock.domain.CardMock;
import com.sleeved.looter.mock.domain.CardWeaknessMock;
import com.sleeved.looter.mock.domain.TypeMock;
import com.sleeved.looter.mock.domain.WeaknessMock;

@ExtendWith(MockitoExtension.class)
class CardWeaknessServiceTest {

  @Mock
  private CardWeaknessRepository cardWeaknessRepository;

  @InjectMocks
  private CardWeaknessService cardWeaknessService;

  @Test
  void getOrCreate_shouldReturnExistingCardWeakness_whenCardWeaknessExists() {
    Type type = TypeMock.createMockTypeSavedInDb(1, "Water");
    Weakness weakness = WeaknessMock.createMockWeaknessSavedInDb(1, type, "×2");
    Card card = CardMock.createBasicMockCard("swsh1-25", "Charmander");

    CardWeakness inputCardWeakness = CardWeaknessMock.createMockCardWeakness(weakness, card);
    CardWeakness existingCardWeakness = CardWeaknessMock.createMockCardWeaknessSavedInDb(1, weakness, card);

    when(cardWeaknessRepository.findByWeaknessAndCard(weakness, card))
        .thenReturn(Optional.of(existingCardWeakness));

    CardWeakness result = cardWeaknessService.getOrCreate(inputCardWeakness);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1);
    assertThat(result.getWeakness()).isEqualTo(weakness);
    assertThat(result.getCard()).isEqualTo(card);
    assertThat(result.getWeakness().getType().getLabel()).isEqualTo("Water");
    assertThat(result.getWeakness().getValue()).isEqualTo("×2");
    assertThat(result.getCard().getName()).isEqualTo("Charmander");

    verify(cardWeaknessRepository).findByWeaknessAndCard(weakness, card);
    verify(cardWeaknessRepository, never()).save(any(CardWeakness.class));
  }

  @Test
  void getOrCreate_shouldCreateAndReturnNewCardWeakness_whenCardWeaknessDoesNotExist() {
    Type type = TypeMock.createMockTypeSavedInDb(2, "Lightning");
    Weakness weakness = WeaknessMock.createMockWeaknessSavedInDb(2, type, "×2");
    Card card = CardMock.createBasicMockCard("swsh2-30", "Squirtle");

    CardWeakness inputCardWeakness = CardWeaknessMock.createMockCardWeakness(weakness, card);
    CardWeakness savedCardWeakness = CardWeaknessMock.createMockCardWeaknessSavedInDb(2, weakness, card);

    when(cardWeaknessRepository.findByWeaknessAndCard(weakness, card))
        .thenReturn(Optional.empty());
    when(cardWeaknessRepository.save(inputCardWeakness)).thenReturn(savedCardWeakness);

    CardWeakness result = cardWeaknessService.getOrCreate(inputCardWeakness);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(2);
    assertThat(result.getWeakness()).isEqualTo(weakness);
    assertThat(result.getCard()).isEqualTo(card);
    assertThat(result.getWeakness().getType().getLabel()).isEqualTo("Lightning");
    assertThat(result.getWeakness().getValue()).isEqualTo("×2");
    assertThat(result.getCard().getName()).isEqualTo("Squirtle");

    verify(cardWeaknessRepository).findByWeaknessAndCard(weakness, card);
    verify(cardWeaknessRepository).save(inputCardWeakness);
  }

  @Test
  void getOrCreate_shouldHandleNullWeakness() {
    Card card = CardMock.createBasicMockCard("swsh3-45", "Bulbasaur");

    CardWeakness inputCardWeakness = CardWeaknessMock.createMockCardWeakness(null, card);
    CardWeakness savedCardWeakness = CardWeaknessMock.createMockCardWeaknessSavedInDb(3, null, card);

    when(cardWeaknessRepository.findByWeaknessAndCard(null, card))
        .thenReturn(Optional.empty());
    when(cardWeaknessRepository.save(inputCardWeakness)).thenReturn(savedCardWeakness);

    CardWeakness result = cardWeaknessService.getOrCreate(inputCardWeakness);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(3);
    assertThat(result.getWeakness()).isNull();
    assertThat(result.getCard()).isEqualTo(card);
    assertThat(result.getCard().getName()).isEqualTo("Bulbasaur");

    verify(cardWeaknessRepository).findByWeaknessAndCard(null, card);
    verify(cardWeaknessRepository).save(inputCardWeakness);
  }

  @Test
  void getOrCreate_shouldHandleNullCard() {
    Type type = TypeMock.createMockTypeSavedInDb(3, "Psychic");
    Weakness weakness = WeaknessMock.createMockWeaknessSavedInDb(3, type, "×2");

    CardWeakness inputCardWeakness = CardWeaknessMock.createMockCardWeakness(weakness, null);
    CardWeakness savedCardWeakness = CardWeaknessMock.createMockCardWeaknessSavedInDb(4, weakness, null);

    when(cardWeaknessRepository.findByWeaknessAndCard(weakness, null))
        .thenReturn(Optional.empty());
    when(cardWeaknessRepository.save(inputCardWeakness)).thenReturn(savedCardWeakness);

    CardWeakness result = cardWeaknessService.getOrCreate(inputCardWeakness);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(4);
    assertThat(result.getWeakness()).isEqualTo(weakness);
    assertThat(result.getWeakness().getType().getLabel()).isEqualTo("Psychic");
    assertThat(result.getWeakness().getValue()).isEqualTo("×2");
    assertThat(result.getCard()).isNull();

    verify(cardWeaknessRepository).findByWeaknessAndCard(weakness, null);
    verify(cardWeaknessRepository).save(inputCardWeakness);
  }

  @Test
  void getOrCreate_shouldHandleBothInputsNull() {
    CardWeakness inputCardWeakness = CardWeaknessMock.createMockCardWeakness(null, null);
    CardWeakness savedCardWeakness = CardWeaknessMock.createMockCardWeaknessSavedInDb(5, null, null);

    when(cardWeaknessRepository.findByWeaknessAndCard(null, null))
        .thenReturn(Optional.empty());
    when(cardWeaknessRepository.save(inputCardWeakness)).thenReturn(savedCardWeakness);

    CardWeakness result = cardWeaknessService.getOrCreate(inputCardWeakness);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(5);
    assertThat(result.getWeakness()).isNull();
    assertThat(result.getCard()).isNull();

    verify(cardWeaknessRepository).findByWeaknessAndCard(null, null);
    verify(cardWeaknessRepository).save(inputCardWeakness);
  }
}