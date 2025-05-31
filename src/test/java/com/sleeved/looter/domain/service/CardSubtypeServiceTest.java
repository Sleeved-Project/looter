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
import com.sleeved.looter.domain.entity.atlas.CardSubtype;
import com.sleeved.looter.domain.entity.atlas.Subtype;
import com.sleeved.looter.domain.repository.atlas.CardSubtypeRepository;
import com.sleeved.looter.mock.domain.CardMock;
import com.sleeved.looter.mock.domain.CardSubtypeMock;
import com.sleeved.looter.mock.domain.SubtypeMock;

@ExtendWith(MockitoExtension.class)
class CardSubtypeServiceTest {

  @Mock
  private CardSubtypeRepository cardSubtypeRepository;

  @InjectMocks
  private CardSubtypeService cardSubtypeService;

  @Test
  void getOrCreate_shouldReturnExistingCardSubtype_whenCardSubtypeExists() {
    Subtype subtype = SubtypeMock.createMockSubtypeSavedInDb(1, "Basic");
    Card card = CardMock.createBasicMockCard("swsh1-25", "Pikachu");

    CardSubtype inputCardSubtype = CardSubtypeMock.createMockCardSubtype(subtype, card);
    CardSubtype existingCardSubtype = CardSubtypeMock.createMockCardSubtypeSavedInDb(1, subtype, card);

    when(cardSubtypeRepository.findBySubtypeAndCard(subtype, card))
        .thenReturn(Optional.of(existingCardSubtype));

    CardSubtype result = cardSubtypeService.getOrCreate(inputCardSubtype);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1);
    assertThat(result.getSubtype()).isEqualTo(subtype);
    assertThat(result.getCard()).isEqualTo(card);
    assertThat(result.getSubtype().getLabel()).isEqualTo("Basic");
    assertThat(result.getCard().getName()).isEqualTo("Pikachu");

    verify(cardSubtypeRepository).findBySubtypeAndCard(subtype, card);
    verify(cardSubtypeRepository, never()).save(any(CardSubtype.class));
  }

  @Test
  void getOrCreate_shouldCreateAndReturnNewCardSubtype_whenCardSubtypeDoesNotExist() {
    Subtype subtype = SubtypeMock.createMockSubtypeSavedInDb(2, "Stage 1");
    Card card = CardMock.createBasicMockCard("swsh2-30", "Raichu");

    CardSubtype inputCardSubtype = CardSubtypeMock.createMockCardSubtype(subtype, card);
    CardSubtype savedCardSubtype = CardSubtypeMock.createMockCardSubtypeSavedInDb(2, subtype, card);

    when(cardSubtypeRepository.findBySubtypeAndCard(subtype, card))
        .thenReturn(Optional.empty());
    when(cardSubtypeRepository.save(inputCardSubtype)).thenReturn(savedCardSubtype);

    CardSubtype result = cardSubtypeService.getOrCreate(inputCardSubtype);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(2);
    assertThat(result.getSubtype()).isEqualTo(subtype);
    assertThat(result.getCard()).isEqualTo(card);
    assertThat(result.getSubtype().getLabel()).isEqualTo("Stage 1");
    assertThat(result.getCard().getName()).isEqualTo("Raichu");

    verify(cardSubtypeRepository).findBySubtypeAndCard(subtype, card);
    verify(cardSubtypeRepository).save(inputCardSubtype);
  }

  @Test
  void getOrCreate_shouldHandleNullSubtype() {
    Card card = CardMock.createBasicMockCard("swsh3-45", "Charizard");

    CardSubtype inputCardSubtype = CardSubtypeMock.createMockCardSubtype(null, card);
    CardSubtype savedCardSubtype = CardSubtypeMock.createMockCardSubtypeSavedInDb(3, null, card);

    when(cardSubtypeRepository.findBySubtypeAndCard(null, card))
        .thenReturn(Optional.empty());
    when(cardSubtypeRepository.save(inputCardSubtype)).thenReturn(savedCardSubtype);

    CardSubtype result = cardSubtypeService.getOrCreate(inputCardSubtype);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(3);
    assertThat(result.getSubtype()).isNull();
    assertThat(result.getCard()).isEqualTo(card);
    assertThat(result.getCard().getName()).isEqualTo("Charizard");

    verify(cardSubtypeRepository).findBySubtypeAndCard(null, card);
    verify(cardSubtypeRepository).save(inputCardSubtype);
  }

  @Test
  void getOrCreate_shouldHandleNullCard() {
    Subtype subtype = SubtypeMock.createMockSubtypeSavedInDb(3, "EX");

    CardSubtype inputCardSubtype = CardSubtypeMock.createMockCardSubtype(subtype, null);
    CardSubtype savedCardSubtype = CardSubtypeMock.createMockCardSubtypeSavedInDb(4, subtype, null);

    when(cardSubtypeRepository.findBySubtypeAndCard(subtype, null))
        .thenReturn(Optional.empty());
    when(cardSubtypeRepository.save(inputCardSubtype)).thenReturn(savedCardSubtype);

    CardSubtype result = cardSubtypeService.getOrCreate(inputCardSubtype);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(4);
    assertThat(result.getSubtype()).isEqualTo(subtype);
    assertThat(result.getSubtype().getLabel()).isEqualTo("EX");
    assertThat(result.getCard()).isNull();

    verify(cardSubtypeRepository).findBySubtypeAndCard(subtype, null);
    verify(cardSubtypeRepository).save(inputCardSubtype);
  }

  @Test
  void getOrCreate_shouldHandleBothInputsNull() {
    CardSubtype inputCardSubtype = CardSubtypeMock.createMockCardSubtype(null, null);
    CardSubtype savedCardSubtype = CardSubtypeMock.createMockCardSubtypeSavedInDb(5, null, null);

    when(cardSubtypeRepository.findBySubtypeAndCard(null, null))
        .thenReturn(Optional.empty());
    when(cardSubtypeRepository.save(inputCardSubtype)).thenReturn(savedCardSubtype);

    CardSubtype result = cardSubtypeService.getOrCreate(inputCardSubtype);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(5);
    assertThat(result.getSubtype()).isNull();
    assertThat(result.getCard()).isNull();

    verify(cardSubtypeRepository).findBySubtypeAndCard(null, null);
    verify(cardSubtypeRepository).save(inputCardSubtype);
  }
}