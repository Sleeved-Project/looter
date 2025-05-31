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
import com.sleeved.looter.domain.entity.atlas.CardType;
import com.sleeved.looter.domain.entity.atlas.Type;
import com.sleeved.looter.domain.repository.atlas.CardTypeRepository;
import com.sleeved.looter.mock.domain.CardMock;
import com.sleeved.looter.mock.domain.CardTypeMock;
import com.sleeved.looter.mock.domain.TypeMock;

@ExtendWith(MockitoExtension.class)
class CardTypeServiceTest {

  @Mock
  private CardTypeRepository cardTypeRepository;

  @InjectMocks
  private CardTypeService cardTypeService;

  @Test
  void getOrCreate_shouldReturnExistingCardType_whenCardTypeExists() {
    Type type = TypeMock.createMockTypeSavedInDb(1, "Electric");
    Card card = CardMock.createBasicMockCard("swsh1-25", "Pikachu");

    CardType inputCardType = CardTypeMock.createMockCardType(type, card);
    CardType existingCardType = CardTypeMock.createMockCardTypeSavedInDb(1, type, card);

    when(cardTypeRepository.findByTypeAndCard(type, card))
        .thenReturn(Optional.of(existingCardType));

    CardType result = cardTypeService.getOrCreate(inputCardType);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1);
    assertThat(result.getType()).isEqualTo(type);
    assertThat(result.getCard()).isEqualTo(card);
    assertThat(result.getType().getLabel()).isEqualTo("Electric");
    assertThat(result.getCard().getName()).isEqualTo("Pikachu");

    verify(cardTypeRepository).findByTypeAndCard(type, card);
    verify(cardTypeRepository, never()).save(any(CardType.class));
  }

  @Test
  void getOrCreate_shouldCreateAndReturnNewCardType_whenCardTypeDoesNotExist() {
    Type type = TypeMock.createMockTypeSavedInDb(2, "Fire");
    Card card = CardMock.createBasicMockCard("swsh2-30", "Charizard");

    CardType inputCardType = CardTypeMock.createMockCardType(type, card);
    CardType savedCardType = CardTypeMock.createMockCardTypeSavedInDb(2, type, card);

    when(cardTypeRepository.findByTypeAndCard(type, card))
        .thenReturn(Optional.empty());
    when(cardTypeRepository.save(inputCardType)).thenReturn(savedCardType);

    CardType result = cardTypeService.getOrCreate(inputCardType);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(2);
    assertThat(result.getType()).isEqualTo(type);
    assertThat(result.getCard()).isEqualTo(card);
    assertThat(result.getType().getLabel()).isEqualTo("Fire");
    assertThat(result.getCard().getName()).isEqualTo("Charizard");

    verify(cardTypeRepository).findByTypeAndCard(type, card);
    verify(cardTypeRepository).save(inputCardType);
  }

  @Test
  void getOrCreate_shouldHandleNullType() {
    Card card = CardMock.createBasicMockCard("swsh3-45", "Mewtwo");

    CardType inputCardType = CardTypeMock.createMockCardType(null, card);
    CardType savedCardType = CardTypeMock.createMockCardTypeSavedInDb(3, null, card);

    when(cardTypeRepository.findByTypeAndCard(null, card))
        .thenReturn(Optional.empty());
    when(cardTypeRepository.save(inputCardType)).thenReturn(savedCardType);

    CardType result = cardTypeService.getOrCreate(inputCardType);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(3);
    assertThat(result.getType()).isNull();
    assertThat(result.getCard()).isEqualTo(card);
    assertThat(result.getCard().getName()).isEqualTo("Mewtwo");

    verify(cardTypeRepository).findByTypeAndCard(null, card);
    verify(cardTypeRepository).save(inputCardType);
  }

  @Test
  void getOrCreate_shouldHandleNullCard() {
    Type type = TypeMock.createMockTypeSavedInDb(3, "Water");

    CardType inputCardType = CardTypeMock.createMockCardType(type, null);
    CardType savedCardType = CardTypeMock.createMockCardTypeSavedInDb(4, type, null);

    when(cardTypeRepository.findByTypeAndCard(type, null))
        .thenReturn(Optional.empty());
    when(cardTypeRepository.save(inputCardType)).thenReturn(savedCardType);

    CardType result = cardTypeService.getOrCreate(inputCardType);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(4);
    assertThat(result.getType()).isEqualTo(type);
    assertThat(result.getType().getLabel()).isEqualTo("Water");
    assertThat(result.getCard()).isNull();

    verify(cardTypeRepository).findByTypeAndCard(type, null);
    verify(cardTypeRepository).save(inputCardType);
  }

  @Test
  void getOrCreate_shouldHandleBothInputsNull() {
    CardType inputCardType = CardTypeMock.createMockCardType(null, null);
    CardType savedCardType = CardTypeMock.createMockCardTypeSavedInDb(5, null, null);

    when(cardTypeRepository.findByTypeAndCard(null, null))
        .thenReturn(Optional.empty());
    when(cardTypeRepository.save(inputCardType)).thenReturn(savedCardType);

    CardType result = cardTypeService.getOrCreate(inputCardType);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(5);
    assertThat(result.getType()).isNull();
    assertThat(result.getCard()).isNull();

    verify(cardTypeRepository).findByTypeAndCard(null, null);
    verify(cardTypeRepository).save(inputCardType);
  }
}