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

import com.sleeved.looter.domain.entity.atlas.Attack;
import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.entity.atlas.CardAttack;
import com.sleeved.looter.domain.service.AttackService;
import com.sleeved.looter.infra.dto.AttackDTO;
import com.sleeved.looter.infra.mapper.AttackMapper;
import com.sleeved.looter.infra.mapper.CardAttackMapper;
import com.sleeved.looter.mock.domain.AttackMock;
import com.sleeved.looter.mock.domain.CardAttackMock;
import com.sleeved.looter.mock.domain.CardMock;
import com.sleeved.looter.mock.infra.AttackDTOMock;

@ExtendWith(MockitoExtension.class)
class CardAttackRelationProcessorTest {

  @Mock
  private AttackMapper attackMapper;

  @Mock
  private AttackService attackService;

  @Mock
  private CardAttackMapper cardAttackMapper;

  @InjectMocks
  private CardAttackRelationProcessor processor;

  private Card card;
  private AttackDTO attackDTO;
  private Attack mappedAttack;
  private Attack foundAttack;
  private CardAttack cardAttack;

  @BeforeEach
  void setUp() {
    // Création des objets de base pour les tests
    card = CardMock.createBasicMockCard("swsh1-25", "Pikachu");

    attackDTO = AttackDTOMock.createMockAttackDTO(
        "Thunderbolt",
        Arrays.asList("Lightning", "Lightning", "Colorless"),
        3,
        "120",
        "Discard all Energy attached to this Pokémon.");

    mappedAttack = AttackMock.createMockAttack(
        "Thunderbolt",
        "120",
        3,
        "Discard all Energy attached to this Pokémon.");

    foundAttack = AttackMock.createMockAttackSavedInDb(
        1,
        "Thunderbolt",
        "120",
        3,
        "Discard all Energy attached to this Pokémon.");

    cardAttack = CardAttackMock.createMockCardAttackSavedInDb(1, foundAttack, card);
  }

  @Test
  void process_shouldReturnEmptyList_whenAttackDTOListIsNull() {
    // Act
    List<CardAttack> result = processor.process(null, card);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result).isEmpty();

    verify(attackMapper, never()).toListEntity(any());
    verify(attackService, never()).getByNameAndDamageAndConvertedEnegyCostAndText(any());
    verify(cardAttackMapper, never()).toEntity(any(), any());
  }

  @Test
  void process_shouldReturnEmptyList_whenAttackDTOListIsEmpty() {
    // Act
    List<CardAttack> result = processor.process(Collections.emptyList(), card);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result).isEmpty();

    verify(attackMapper, never()).toListEntity(any());
    verify(attackService, never()).getByNameAndDamageAndConvertedEnegyCostAndText(any());
    verify(cardAttackMapper, never()).toEntity(any(), any());
  }

  @Test
  void process_shouldProcessSingleAttackDTO_whenListContainsOneItem() {
    // Arrange
    List<AttackDTO> attackDTOs = Collections.singletonList(attackDTO);
    List<Attack> mappedAttacks = Collections.singletonList(mappedAttack);

    when(attackMapper.toListEntity(attackDTOs)).thenReturn(mappedAttacks);
    when(attackService.getByNameAndDamageAndConvertedEnegyCostAndText(mappedAttack)).thenReturn(foundAttack);
    when(cardAttackMapper.toEntity(foundAttack, card)).thenReturn(cardAttack);

    // Act
    List<CardAttack> result = processor.process(attackDTOs, card);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0)).isEqualTo(cardAttack);
    assertThat(result.get(0).getAttack()).isEqualTo(foundAttack);
    assertThat(result.get(0).getCard()).isEqualTo(card);

    verify(attackMapper).toListEntity(attackDTOs);
    verify(attackService).getByNameAndDamageAndConvertedEnegyCostAndText(mappedAttack);
    verify(cardAttackMapper).toEntity(foundAttack, card);
  }

  @Test
  void process_shouldProcessMultipleAttackDTOs_whenListContainsMultipleItems() {
    // Arrange
    AttackDTO attackDTO2 = AttackDTOMock.createMockAttackDTO(
        "Quick Attack",
        Arrays.asList("Lightning", "Colorless"),
        2,
        "30+",
        "Flip a coin. If heads, this attack does 30 more damage.");

    List<AttackDTO> attackDTOs = Arrays.asList(attackDTO, attackDTO2);

    Attack mappedAttack2 = AttackMock.createMockAttack(
        "Quick Attack",
        "30+",
        2,
        "Flip a coin. If heads, this attack does 30 more damage.");

    List<Attack> mappedAttacks = Arrays.asList(mappedAttack, mappedAttack2);

    Attack foundAttack2 = AttackMock.createMockAttackSavedInDb(
        2,
        "Quick Attack",
        "30+",
        2,
        "Flip a coin. If heads, this attack does 30 more damage.");

    CardAttack cardAttack2 = CardAttackMock.createMockCardAttackSavedInDb(2, foundAttack2, card);

    when(attackMapper.toListEntity(attackDTOs)).thenReturn(mappedAttacks);
    when(attackService.getByNameAndDamageAndConvertedEnegyCostAndText(mappedAttack)).thenReturn(foundAttack);
    when(attackService.getByNameAndDamageAndConvertedEnegyCostAndText(mappedAttack2)).thenReturn(foundAttack2);
    when(cardAttackMapper.toEntity(foundAttack, card)).thenReturn(cardAttack);
    when(cardAttackMapper.toEntity(foundAttack2, card)).thenReturn(cardAttack2);

    // Act
    List<CardAttack> result = processor.process(attackDTOs, card);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);
    assertThat(result.get(0).getAttack().getName()).isEqualTo("Thunderbolt");
    assertThat(result.get(1).getAttack().getName()).isEqualTo("Quick Attack");

    verify(attackMapper).toListEntity(attackDTOs);
    verify(attackService, times(2)).getByNameAndDamageAndConvertedEnegyCostAndText(any(Attack.class));
    verify(cardAttackMapper, times(2)).toEntity(any(Attack.class), any(Card.class));
  }

  @Test
  void process_shouldSkipNull_whenCardAttackMapperReturnsNull() {
    // Arrange
    List<AttackDTO> attackDTOs = Collections.singletonList(attackDTO);
    List<Attack> mappedAttacks = Collections.singletonList(mappedAttack);

    when(attackMapper.toListEntity(attackDTOs)).thenReturn(mappedAttacks);
    when(attackService.getByNameAndDamageAndConvertedEnegyCostAndText(mappedAttack)).thenReturn(foundAttack);
    when(cardAttackMapper.toEntity(foundAttack, card)).thenReturn(null); // Retourne null

    // Act
    List<CardAttack> result = processor.process(attackDTOs, card);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result).isEmpty();

    verify(attackMapper).toListEntity(attackDTOs);
    verify(attackService).getByNameAndDamageAndConvertedEnegyCostAndText(mappedAttack);
    verify(cardAttackMapper).toEntity(foundAttack, card);
  }
}