package com.sleeved.looter.infra.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sleeved.looter.domain.entity.atlas.Ability;
import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.entity.atlas.CardAbility;
import com.sleeved.looter.domain.service.AbilityService;
import com.sleeved.looter.infra.dto.AbilityDTO;
import com.sleeved.looter.infra.mapper.AbilityMapper;
import com.sleeved.looter.infra.mapper.CardAbilityMapper;
import com.sleeved.looter.mock.domain.AbilityMock;
import com.sleeved.looter.mock.domain.CardAbilityMock;
import com.sleeved.looter.mock.domain.CardMock;
import com.sleeved.looter.mock.infra.AbilityDTOMock;

@ExtendWith(MockitoExtension.class)
class CardAbilityRelationProcessorTest {

  @Mock
  private AbilityMapper abilityMapper;

  @Mock
  private AbilityService abilityService;

  @Mock
  private CardAbilityMapper cardAbilityMapper;

  @InjectMocks
  private CardAbilityRelationProcessor processor;

  private Card card;
  private AbilityDTO abilityDTO;
  private Ability mappedAbility;
  private Ability foundAbility;
  private CardAbility cardAbility;

  @BeforeEach
  void setUp() {
    card = CardMock.createBasicMockCard("swsh1-25", "Pikachu");

    abilityDTO = AbilityDTOMock.createMockAbilityDTO(
        "Thunder Shock",
        "Once during your turn, you may have your opponent switch their Active Pokémon.",
        "Ability");

    mappedAbility = AbilityMock.createMockAbility(
        "Thunder Shock",
        "Once during your turn, you may have your opponent switch their Active Pokémon.",
        "Ability");

    foundAbility = AbilityMock.createMockAbilitySavedInDb(
        1,
        "Thunder Shock",
        "Once during your turn, you may have your opponent switch their Active Pokémon.",
        "Ability");

    cardAbility = CardAbilityMock.createMockCardAbilitySavedInDb(1, foundAbility, card);
  }

  @Test
  void process_shouldReturnEmptyList_whenAbilityDTOListIsNull() {
    List<CardAbility> result = processor.process(null, card);

    assertThat(result).isNotNull();
    assertThat(result).isEmpty();

    verify(abilityMapper, never()).toListEntity(any());
    verify(abilityService, never()).getByNameAndTypeAndText(any());
    verify(cardAbilityMapper, never()).toEntity(any(), any());
  }

  @Test
  void process_shouldReturnEmptyList_whenAbilityDTOListIsEmpty() {
    List<CardAbility> result = processor.process(Collections.emptyList(), card);

    assertThat(result).isNotNull();
    assertThat(result).isEmpty();

    verify(abilityMapper, never()).toListEntity(any());
    verify(abilityService, never()).getByNameAndTypeAndText(any());
    verify(cardAbilityMapper, never()).toEntity(any(), any());
  }

  @Test
  void process_shouldProcessSingleAbilityDTO_whenListContainsOneItem() {
    List<AbilityDTO> abilityDTOs = Collections.singletonList(abilityDTO);
    List<Ability> mappedAbilities = Collections.singletonList(mappedAbility);

    when(abilityMapper.toListEntity(abilityDTOs)).thenReturn(mappedAbilities);
    when(abilityService.getByNameAndTypeAndText(mappedAbility)).thenReturn(foundAbility);
    when(cardAbilityMapper.toEntity(foundAbility, card)).thenReturn(cardAbility);

    List<CardAbility> result = processor.process(abilityDTOs, card);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0)).isEqualTo(cardAbility);
    assertThat(result.get(0).getAbility()).isEqualTo(foundAbility);
    assertThat(result.get(0).getCard()).isEqualTo(card);

    verify(abilityMapper).toListEntity(abilityDTOs);
    verify(abilityService).getByNameAndTypeAndText(mappedAbility);
    verify(cardAbilityMapper).toEntity(foundAbility, card);
  }

  @Test
  void process_shouldProcessMultipleAbilityDTOs_whenListContainsMultipleItems() {
    AbilityDTO abilityDTO2 = AbilityDTOMock.createMockAbilityDTO(
        "Lightning Rod",
        "Once during your turn, draw a card.",
        "Pokémon Power");
    List<AbilityDTO> abilityDTOs = Arrays.asList(abilityDTO, abilityDTO2);

    Ability mappedAbility2 = AbilityMock.createMockAbility(
        "Lightning Rod",
        "Once during your turn, draw a card.",
        "Pokémon Power");
    List<Ability> mappedAbilities = Arrays.asList(mappedAbility, mappedAbility2);

    Ability foundAbility2 = AbilityMock.createMockAbilitySavedInDb(
        2,
        "Lightning Rod",
        "Once during your turn, draw a card.",
        "Pokémon Power");

    CardAbility cardAbility2 = CardAbilityMock.createMockCardAbilitySavedInDb(2, foundAbility2, card);

    when(abilityMapper.toListEntity(abilityDTOs)).thenReturn(mappedAbilities);
    when(abilityService.getByNameAndTypeAndText(mappedAbility)).thenReturn(foundAbility);
    when(abilityService.getByNameAndTypeAndText(mappedAbility2)).thenReturn(foundAbility2);
    when(cardAbilityMapper.toEntity(foundAbility, card)).thenReturn(cardAbility);
    when(cardAbilityMapper.toEntity(foundAbility2, card)).thenReturn(cardAbility2);

    List<CardAbility> result = processor.process(abilityDTOs, card);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);
    assertThat(result.get(0).getAbility().getName()).isEqualTo("Thunder Shock");
    assertThat(result.get(0).getAbility().getType()).isEqualTo("Ability");
    assertThat(result.get(1).getAbility().getName()).isEqualTo("Lightning Rod");
    assertThat(result.get(1).getAbility().getType()).isEqualTo("Pokémon Power");

    verify(abilityMapper).toListEntity(abilityDTOs);
    verify(abilityService, times(2)).getByNameAndTypeAndText(any(Ability.class));
    verify(cardAbilityMapper, times(2)).toEntity(any(Ability.class), any(Card.class));
  }

  @Test
  void process_shouldSkipNull_whenCardAbilityMapperReturnsNull() {
    List<AbilityDTO> abilityDTOs = Collections.singletonList(abilityDTO);
    List<Ability> mappedAbilities = Collections.singletonList(mappedAbility);

    when(abilityMapper.toListEntity(abilityDTOs)).thenReturn(mappedAbilities);
    when(abilityService.getByNameAndTypeAndText(mappedAbility)).thenReturn(foundAbility);

    List<CardAbility> result = processor.process(abilityDTOs, card);

    assertThat(result).isNotNull();
    assertThat(result).isEmpty();

    verify(abilityMapper).toListEntity(abilityDTOs);
    verify(abilityService).getByNameAndTypeAndText(mappedAbility);
    verify(cardAbilityMapper).toEntity(foundAbility, card);
  }

  @Test
  void process_shouldHandleMixedResults_whenSomeCardAbilitiesAreNullAndSomeAreNot() {
    AbilityDTO abilityDTO1 = AbilityDTOMock.createMockAbilityDTO(
        "Thunder Shock",
        "Once during your turn, you may have your opponent switch their Active Pokémon.",
        "Ability");

    AbilityDTO abilityDTO2 = AbilityDTOMock.createMockAbilityDTO(
        "Lightning Rod",
        "Once during your turn, draw a card.",
        "Pokémon Power");

    List<AbilityDTO> abilityDTOs = Arrays.asList(abilityDTO1, abilityDTO2);

    Ability mappedAbility1 = AbilityMock.createMockAbility(
        "Thunder Shock",
        "Once during your turn, you may have your opponent switch their Active Pokémon.",
        "Ability");

    Ability mappedAbility2 = AbilityMock.createMockAbility(
        "Lightning Rod",
        "Once during your turn, draw a card.",
        "Pokémon Power");

    List<Ability> mappedAbilities = Arrays.asList(mappedAbility1, mappedAbility2);

    Ability foundAbility1 = AbilityMock.createMockAbilitySavedInDb(
        1,
        "Thunder Shock",
        "Once during your turn, you may have your opponent switch their Active Pokémon.",
        "Ability");

    Ability foundAbility2 = AbilityMock.createMockAbilitySavedInDb(
        2,
        "Lightning Rod",
        "Once during your turn, draw a card.",
        "Pokémon Power");

    CardAbility cardAbility1 = CardAbilityMock.createMockCardAbilitySavedInDb(1, foundAbility1, card);

    when(abilityMapper.toListEntity(abilityDTOs)).thenReturn(mappedAbilities);
    when(abilityService.getByNameAndTypeAndText(mappedAbility1)).thenReturn(foundAbility1);
    when(abilityService.getByNameAndTypeAndText(mappedAbility2)).thenReturn(foundAbility2);
    when(cardAbilityMapper.toEntity(foundAbility1, card)).thenReturn(cardAbility1);

    List<CardAbility> result = processor.process(abilityDTOs, card);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getAbility().getName()).isEqualTo("Thunder Shock");

    verify(abilityMapper).toListEntity(abilityDTOs);
    verify(abilityService, times(2)).getByNameAndTypeAndText(any(Ability.class));
    verify(cardAbilityMapper, times(2)).toEntity(any(Ability.class), any(Card.class));
  }
}