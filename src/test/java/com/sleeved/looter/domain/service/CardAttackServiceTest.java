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

import com.sleeved.looter.domain.entity.atlas.Attack;
import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.entity.atlas.CardAttack;
import com.sleeved.looter.domain.repository.atlas.CardAttackRepository;
import com.sleeved.looter.mock.domain.AttackMock;
import com.sleeved.looter.mock.domain.CardAttackMock;
import com.sleeved.looter.mock.domain.CardMock;

@ExtendWith(MockitoExtension.class)
class CardAttackServiceTest {

  @Mock
  private CardAttackRepository cardAttackRepository;

  @InjectMocks
  private CardAttackService cardAttackService;

  @Test
  void getOrCreate_shouldReturnExistingCardAttack_whenCardAttackExists() {
    Attack attack = AttackMock.createMockAttackSavedInDb(1, "Thunderbolt", "120", 3,
        "Discard all Energy attached to this Pokémon");
    Card card = CardMock.createBasicMockCard("sm10-72", "Pikachu");

    CardAttack inputCardAttack = CardAttackMock.createMockCardAttack(attack, card);
    CardAttack existingCardAttack = CardAttackMock.createMockCardAttackSavedInDb(1, attack, card);

    when(cardAttackRepository.findByAttackAndCard(attack, card))
        .thenReturn(Optional.of(existingCardAttack));

    CardAttack result = cardAttackService.getOrCreate(inputCardAttack);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1);
    assertThat(result.getAttack()).isEqualTo(attack);
    assertThat(result.getCard()).isEqualTo(card);
    assertThat(result.getAttack().getName()).isEqualTo("Thunderbolt");
    assertThat(result.getCard().getName()).isEqualTo("Pikachu");

    verify(cardAttackRepository).findByAttackAndCard(attack, card);
    verify(cardAttackRepository, never()).save(any(CardAttack.class));
  }

  @Test
  void getOrCreate_shouldCreateAndReturnNewCardAttack_whenCardAttackDoesNotExist() {
    Attack attack = AttackMock.createMockAttackSavedInDb(2, "Flamethrower", "90", 3,
        "Discard 1 Fire Energy attached to this Pokémon");
    Card card = CardMock.createBasicMockCard("sm12-25", "Charizard");

    CardAttack inputCardAttack = CardAttackMock.createMockCardAttack(attack, card);
    CardAttack savedCardAttack = CardAttackMock.createMockCardAttackSavedInDb(2, attack, card);

    when(cardAttackRepository.findByAttackAndCard(attack, card))
        .thenReturn(Optional.empty());
    when(cardAttackRepository.save(inputCardAttack)).thenReturn(savedCardAttack);

    CardAttack result = cardAttackService.getOrCreate(inputCardAttack);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(2);
    assertThat(result.getAttack()).isEqualTo(attack);
    assertThat(result.getCard()).isEqualTo(card);
    assertThat(result.getAttack().getName()).isEqualTo("Flamethrower");
    assertThat(result.getCard().getName()).isEqualTo("Charizard");

    verify(cardAttackRepository).findByAttackAndCard(attack, card);
    verify(cardAttackRepository).save(inputCardAttack);
  }

  @Test
  void getOrCreate_shouldHandleNullAttack() {
    Card card = CardMock.createBasicMockCard("sm11-56", "Eevee");

    CardAttack inputCardAttack = CardAttackMock.createMockCardAttack(null, card);
    CardAttack savedCardAttack = CardAttackMock.createMockCardAttackSavedInDb(3, null, card);

    when(cardAttackRepository.findByAttackAndCard(null, card))
        .thenReturn(Optional.empty());
    when(cardAttackRepository.save(inputCardAttack)).thenReturn(savedCardAttack);

    CardAttack result = cardAttackService.getOrCreate(inputCardAttack);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(3);
    assertThat(result.getAttack()).isNull();
    assertThat(result.getCard()).isEqualTo(card);
    assertThat(result.getCard().getName()).isEqualTo("Eevee");

    verify(cardAttackRepository).findByAttackAndCard(null, card);
    verify(cardAttackRepository).save(inputCardAttack);
  }

  @Test
  void getOrCreate_shouldHandleNullCard() {
    Attack attack = AttackMock.createMockAttackSavedInDb(3, "Hydro Pump", "130", 4,
        "This attack does 30 more damage for each Water Energy attached to this Pokémon");

    CardAttack inputCardAttack = CardAttackMock.createMockCardAttack(attack, null);
    CardAttack savedCardAttack = CardAttackMock.createMockCardAttackSavedInDb(4, attack, null);

    when(cardAttackRepository.findByAttackAndCard(attack, null))
        .thenReturn(Optional.empty());
    when(cardAttackRepository.save(inputCardAttack)).thenReturn(savedCardAttack);

    CardAttack result = cardAttackService.getOrCreate(inputCardAttack);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(4);
    assertThat(result.getAttack()).isEqualTo(attack);
    assertThat(result.getAttack().getName()).isEqualTo("Hydro Pump");
    assertThat(result.getCard()).isNull();

    verify(cardAttackRepository).findByAttackAndCard(attack, null);
    verify(cardAttackRepository).save(inputCardAttack);
  }

  @Test
  void getOrCreate_shouldHandleBothInputsNull() {
    CardAttack inputCardAttack = CardAttackMock.createMockCardAttack(null, null);
    CardAttack savedCardAttack = CardAttackMock.createMockCardAttackSavedInDb(5, null, null);

    when(cardAttackRepository.findByAttackAndCard(null, null))
        .thenReturn(Optional.empty());
    when(cardAttackRepository.save(inputCardAttack)).thenReturn(savedCardAttack);

    CardAttack result = cardAttackService.getOrCreate(inputCardAttack);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(5);
    assertThat(result.getAttack()).isNull();
    assertThat(result.getCard()).isNull();

    verify(cardAttackRepository).findByAttackAndCard(null, null);
    verify(cardAttackRepository).save(inputCardAttack);
  }
}