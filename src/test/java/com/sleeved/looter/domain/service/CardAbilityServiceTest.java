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

import com.sleeved.looter.domain.entity.atlas.Ability;
import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.entity.atlas.CardAbility;
import com.sleeved.looter.domain.repository.atlas.CardAbilityRepository;
import com.sleeved.looter.mock.domain.AbilityMock;
import com.sleeved.looter.mock.domain.CardAbilityMock;
import com.sleeved.looter.mock.domain.CardMock;

@ExtendWith(MockitoExtension.class)
class CardAbilityServiceTest {

  @Mock
  private CardAbilityRepository cardAbilityRepository;

  @InjectMocks
  private CardAbilityService cardAbilityService;

  @Test
  void getOrCreate_shouldReturnExistingCardAbility_whenCardAbilityExists() {
    Ability ability = AbilityMock.createMockAbilitySavedInDb(1, "Lightning Rod",
        "When your opponent plays a Trainer card, prevent all effects of that card done to this Pokémon.",
        "Ability");
    Card card = CardMock.createBasicMockCard("sm10-72", "Pikachu");

    CardAbility inputCardAbility = CardAbilityMock.createMockCardAbility(ability, card);
    CardAbility existingCardAbility = CardAbilityMock.createMockCardAbilitySavedInDb(1, ability, card);

    when(cardAbilityRepository.findByAbilityAndCard(ability, card))
        .thenReturn(Optional.of(existingCardAbility));

    CardAbility result = cardAbilityService.getOrCreate(inputCardAbility);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1);
    assertThat(result.getAbility()).isEqualTo(ability);
    assertThat(result.getCard()).isEqualTo(card);
    assertThat(result.getAbility().getName()).isEqualTo("Lightning Rod");
    assertThat(result.getCard().getName()).isEqualTo("Pikachu");

    verify(cardAbilityRepository).findByAbilityAndCard(ability, card);
    verify(cardAbilityRepository, never()).save(any(CardAbility.class));
  }

  @Test
  void getOrCreate_shouldCreateAndReturnNewCardAbility_whenCardAbilityDoesNotExist() {
    Ability ability = AbilityMock.createMockAbilitySavedInDb(2, "Blaze",
        "When this Pokémon has 30 HP or less remaining, this Pokémon's attacks do 50 more damage.",
        "Ability");
    Card card = CardMock.createBasicMockCard("sm12-25", "Charizard");

    CardAbility inputCardAbility = CardAbilityMock.createMockCardAbility(ability, card);
    CardAbility savedCardAbility = CardAbilityMock.createMockCardAbilitySavedInDb(2, ability, card);

    when(cardAbilityRepository.findByAbilityAndCard(ability, card))
        .thenReturn(Optional.empty());
    when(cardAbilityRepository.save(inputCardAbility)).thenReturn(savedCardAbility);

    CardAbility result = cardAbilityService.getOrCreate(inputCardAbility);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(2);
    assertThat(result.getAbility()).isEqualTo(ability);
    assertThat(result.getCard()).isEqualTo(card);
    assertThat(result.getAbility().getName()).isEqualTo("Blaze");
    assertThat(result.getCard().getName()).isEqualTo("Charizard");

    verify(cardAbilityRepository).findByAbilityAndCard(ability, card);
    verify(cardAbilityRepository).save(inputCardAbility);
  }

  @Test
  void getOrCreate_shouldHandleNullAbility() {
    Card card = CardMock.createBasicMockCard("sm11-56", "Eevee");

    CardAbility inputCardAbility = CardAbilityMock.createMockCardAbility(null, card);
    CardAbility savedCardAbility = CardAbilityMock.createMockCardAbilitySavedInDb(3, null, card);

    when(cardAbilityRepository.findByAbilityAndCard(null, card))
        .thenReturn(Optional.empty());
    when(cardAbilityRepository.save(inputCardAbility)).thenReturn(savedCardAbility);

    CardAbility result = cardAbilityService.getOrCreate(inputCardAbility);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(3);
    assertThat(result.getAbility()).isNull();
    assertThat(result.getCard()).isEqualTo(card);
    assertThat(result.getCard().getName()).isEqualTo("Eevee");

    verify(cardAbilityRepository).findByAbilityAndCard(null, card);
    verify(cardAbilityRepository).save(inputCardAbility);
  }

  @Test
  void getOrCreate_shouldHandleNullCard() {
    Ability ability = AbilityMock.createMockAbilitySavedInDb(3, "Adaptability",
        "This Pokémon's attacks do 20 more damage to your opponent's Active Pokémon.",
        "Ability");

    CardAbility inputCardAbility = CardAbilityMock.createMockCardAbility(ability, null);
    CardAbility savedCardAbility = CardAbilityMock.createMockCardAbilitySavedInDb(4, ability, null);

    when(cardAbilityRepository.findByAbilityAndCard(ability, null))
        .thenReturn(Optional.empty());
    when(cardAbilityRepository.save(inputCardAbility)).thenReturn(savedCardAbility);

    CardAbility result = cardAbilityService.getOrCreate(inputCardAbility);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(4);
    assertThat(result.getAbility()).isEqualTo(ability);
    assertThat(result.getAbility().getName()).isEqualTo("Adaptability");
    assertThat(result.getCard()).isNull();

    verify(cardAbilityRepository).findByAbilityAndCard(ability, null);
    verify(cardAbilityRepository).save(inputCardAbility);
  }

  @Test
  void getOrCreate_shouldHandleBothInputsNull() {
    CardAbility inputCardAbility = CardAbilityMock.createMockCardAbility(null, null);
    CardAbility savedCardAbility = CardAbilityMock.createMockCardAbilitySavedInDb(5, null, null);

    when(cardAbilityRepository.findByAbilityAndCard(null, null))
        .thenReturn(Optional.empty());
    when(cardAbilityRepository.save(inputCardAbility)).thenReturn(savedCardAbility);

    CardAbility result = cardAbilityService.getOrCreate(inputCardAbility);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(5);
    assertThat(result.getAbility()).isNull();
    assertThat(result.getCard()).isNull();

    verify(cardAbilityRepository).findByAbilityAndCard(null, null);
    verify(cardAbilityRepository).save(inputCardAbility);
  }
}