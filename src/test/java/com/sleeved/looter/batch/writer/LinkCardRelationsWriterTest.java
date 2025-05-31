package com.sleeved.looter.batch.writer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.Chunk;

import com.sleeved.looter.common.util.Constantes;
import com.sleeved.looter.domain.entity.atlas.Ability;
import com.sleeved.looter.domain.entity.atlas.Attack;
import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.entity.atlas.CardAbility;
import com.sleeved.looter.domain.entity.atlas.CardAttack;
import com.sleeved.looter.domain.entity.atlas.CardResistance;
import com.sleeved.looter.domain.entity.atlas.CardSubtype;
import com.sleeved.looter.domain.entity.atlas.CardType;
import com.sleeved.looter.domain.entity.atlas.CardWeakness;
import com.sleeved.looter.domain.entity.atlas.Resistance;
import com.sleeved.looter.domain.entity.atlas.Subtype;
import com.sleeved.looter.domain.entity.atlas.Type;
import com.sleeved.looter.domain.entity.atlas.Weakness;
import com.sleeved.looter.domain.service.CardAbilityService;
import com.sleeved.looter.domain.service.CardAttackService;
import com.sleeved.looter.domain.service.CardResistanceService;
import com.sleeved.looter.domain.service.CardSubtypeService;
import com.sleeved.looter.domain.service.CardTypeService;
import com.sleeved.looter.domain.service.CardWeaknessService;
import com.sleeved.looter.infra.dto.LinkCardRelationsEntitiesProcessedDTO;
import com.sleeved.looter.infra.service.LooterScrapingErrorHandler;
import com.sleeved.looter.mock.domain.AbilityMock;
import com.sleeved.looter.mock.domain.AttackMock;
import com.sleeved.looter.mock.domain.CardAbilityMock;
import com.sleeved.looter.mock.domain.CardAttackMock;
import com.sleeved.looter.mock.domain.CardMock;
import com.sleeved.looter.mock.domain.CardResistanceMock;
import com.sleeved.looter.mock.domain.CardSubtypeMock;
import com.sleeved.looter.mock.domain.CardTypeMock;
import com.sleeved.looter.mock.domain.CardWeaknessMock;
import com.sleeved.looter.mock.domain.ResistanceMock;
import com.sleeved.looter.mock.domain.SubtypeMock;
import com.sleeved.looter.mock.domain.TypeMock;
import com.sleeved.looter.mock.domain.WeaknessMock;

@ExtendWith(MockitoExtension.class)
class LinkCardRelationsWriterTest {

  @Mock
  private CardAbilityService cardAbilityService;

  @Mock
  private CardAttackService cardAttackService;

  @Mock
  private CardResistanceService cardResistanceService;

  @Mock
  private CardSubtypeService cardSubtypeService;

  @Mock
  private CardTypeService cardTypeService;

  @Mock
  private CardWeaknessService cardWeaknessService;

  @Mock
  private LooterScrapingErrorHandler looterScrapingErrorHandler;

  @InjectMocks
  private LinkCardRelationsWriter writer;

  private LinkCardRelationsEntitiesProcessedDTO dto;
  private Card card;
  private CardAbility cardAbility;
  private CardAttack cardAttack;
  private CardResistance cardResistance;
  private CardSubtype cardSubtype;
  private CardType cardType;
  private CardWeakness cardWeakness;

  @BeforeEach
  void setUp() {
    card = CardMock.createBasicMockCard("swsh1-25", "Pikachu");

    Ability ability = AbilityMock.createMockAbilitySavedInDb(1, "Thunder Shock",
        "Once during your turn, you may have your opponent switch their Active Pokémon.", "Ability");
    Attack attack = AttackMock.createMockAttackSavedInDb(1, "Thunderbolt", "120", 3,
        "Discard all Energy attached to this Pokémon");
    Type electricType = TypeMock.createMockTypeSavedInDb(1, "Electric");
    Type fightingType = TypeMock.createMockTypeSavedInDb(2, "Fighting");
    Resistance resistance = ResistanceMock.createMockResistanceSavedInDb(1, fightingType, "-30");
    Weakness weakness = WeaknessMock.createMockWeaknessSavedInDb(1, electricType, "×2");
    Subtype subtype = SubtypeMock.createMockSubtypeSavedInDb(1, "Basic");

    cardAbility = CardAbilityMock.createMockCardAbilitySavedInDb(1, ability, card);
    cardAttack = CardAttackMock.createMockCardAttackSavedInDb(1, attack, card);
    cardResistance = CardResistanceMock.createMockCardResistanceSavedInDb(1, resistance, card);
    cardSubtype = CardSubtypeMock.createMockCardSubtypeSavedInDb(1, subtype, card);
    cardType = CardTypeMock.createMockCardTypeSavedInDb(1, electricType, card);
    cardWeakness = CardWeaknessMock.createMockCardWeaknessSavedInDb(1, weakness, card);

    dto = new LinkCardRelationsEntitiesProcessedDTO();
    dto.setCardAbilities(Arrays.asList(cardAbility));
    dto.setCardAttacks(Arrays.asList(cardAttack));
    dto.setCardResistances(Arrays.asList(cardResistance));
    dto.setCardSubtypes(Arrays.asList(cardSubtype));
    dto.setCardTypes(Arrays.asList(cardType));
    dto.setCardWeaknesses(Arrays.asList(cardWeakness));
  }

  @Test
  void write_shouldProcessAllEntitiesInChunk_whenNoExceptionsOccur() throws Exception {
    Chunk<LinkCardRelationsEntitiesProcessedDTO> chunk = new Chunk<>(Collections.singletonList(dto));

    when(cardAbilityService.getOrCreate(any(CardAbility.class))).thenReturn(cardAbility);
    when(cardAttackService.getOrCreate(any(CardAttack.class))).thenReturn(cardAttack);
    when(cardResistanceService.getOrCreate(any(CardResistance.class))).thenReturn(cardResistance);
    when(cardSubtypeService.getOrCreate(any(CardSubtype.class))).thenReturn(cardSubtype);
    when(cardTypeService.getOrCreate(any(CardType.class))).thenReturn(cardType);
    when(cardWeaknessService.getOrCreate(any(CardWeakness.class))).thenReturn(cardWeakness);

    writer.write(chunk);

    verify(cardAbilityService, times(1)).getOrCreate(cardAbility);
    verify(cardAttackService, times(1)).getOrCreate(cardAttack);
    verify(cardResistanceService, times(1)).getOrCreate(cardResistance);
    verify(cardSubtypeService, times(1)).getOrCreate(cardSubtype);
    verify(cardTypeService, times(1)).getOrCreate(cardType);
    verify(cardWeaknessService, times(1)).getOrCreate(cardWeakness);

    verify(looterScrapingErrorHandler, never()).formatErrorItem(anyString(), anyString());
    verify(looterScrapingErrorHandler, never()).handle(any(Exception.class), anyString(), anyString(), anyString());
  }

  @Test
  void write_shouldHandleMultipleEntities_whenChunkContainsMultipleItems() throws Exception {
    LinkCardRelationsEntitiesProcessedDTO dto2 = new LinkCardRelationsEntitiesProcessedDTO();
    dto2.setCardAbilities(Arrays.asList(cardAbility));
    dto2.setCardAttacks(Arrays.asList(cardAttack));
    dto2.setCardResistances(Arrays.asList(cardResistance));
    dto2.setCardSubtypes(Arrays.asList(cardSubtype));
    dto2.setCardTypes(Arrays.asList(cardType));
    dto2.setCardWeaknesses(Arrays.asList(cardWeakness));

    Chunk<LinkCardRelationsEntitiesProcessedDTO> chunk = new Chunk<>(Arrays.asList(dto, dto2));

    when(cardAbilityService.getOrCreate(any(CardAbility.class))).thenReturn(cardAbility);
    when(cardAttackService.getOrCreate(any(CardAttack.class))).thenReturn(cardAttack);
    when(cardResistanceService.getOrCreate(any(CardResistance.class))).thenReturn(cardResistance);
    when(cardSubtypeService.getOrCreate(any(CardSubtype.class))).thenReturn(cardSubtype);
    when(cardTypeService.getOrCreate(any(CardType.class))).thenReturn(cardType);
    when(cardWeaknessService.getOrCreate(any(CardWeakness.class))).thenReturn(cardWeakness);

    writer.write(chunk);

    verify(cardAbilityService, times(2)).getOrCreate(cardAbility);
    verify(cardAttackService, times(2)).getOrCreate(cardAttack);
    verify(cardResistanceService, times(2)).getOrCreate(cardResistance);
    verify(cardSubtypeService, times(2)).getOrCreate(cardSubtype);
    verify(cardTypeService, times(2)).getOrCreate(cardType);
    verify(cardWeaknessService, times(2)).getOrCreate(cardWeakness);
  }

  @Test
  void write_shouldHandleEmptyCollections_whenDTOHasEmptyCollections() throws Exception {
    LinkCardRelationsEntitiesProcessedDTO emptyDto = new LinkCardRelationsEntitiesProcessedDTO();
    emptyDto.setCardAbilities(Collections.emptyList());
    emptyDto.setCardAttacks(Collections.emptyList());
    emptyDto.setCardResistances(Collections.emptyList());
    emptyDto.setCardSubtypes(Collections.emptyList());
    emptyDto.setCardTypes(Collections.emptyList());
    emptyDto.setCardWeaknesses(Collections.emptyList());

    Chunk<LinkCardRelationsEntitiesProcessedDTO> chunk = new Chunk<>(Collections.singletonList(emptyDto));

    writer.write(chunk);

    verify(cardAbilityService, never()).getOrCreate(any(CardAbility.class));
    verify(cardAttackService, never()).getOrCreate(any(CardAttack.class));
    verify(cardResistanceService, never()).getOrCreate(any(CardResistance.class));
    verify(cardSubtypeService, never()).getOrCreate(any(CardSubtype.class));
    verify(cardTypeService, never()).getOrCreate(any(CardType.class));
    verify(cardWeaknessService, never()).getOrCreate(any(CardWeakness.class));
  }

  @Test
  void write_shouldHandleExceptionAndContinue_whenServiceThrowsException() throws Exception {
    Chunk<LinkCardRelationsEntitiesProcessedDTO> chunk = new Chunk<>(Collections.singletonList(dto));

    when(cardAbilityService.getOrCreate(any(CardAbility.class))).thenThrow(new RuntimeException("Service error"));
    when(looterScrapingErrorHandler.formatErrorItem(anyString(), anyString())).thenReturn("Formatted error");
    doNothing().when(looterScrapingErrorHandler).handle(any(), anyString(), anyString(), anyString());

    writer.write(chunk);

    verify(looterScrapingErrorHandler).formatErrorItem(
        eq(Constantes.LINK_CARD_RELATIONS_ENTITIES_ITEM), anyString());
    verify(looterScrapingErrorHandler).handle(
        any(RuntimeException.class),
        eq(Constantes.LINK_CARD_RELATIONS_ENTITIES_WRITER_CONTEXT),
        eq(Constantes.WRITE_ACTION),
        anyString());

    verify(cardAttackService, never()).getOrCreate(any(CardAttack.class));
    verify(cardResistanceService, never()).getOrCreate(any(CardResistance.class));
    verify(cardSubtypeService, never()).getOrCreate(any(CardSubtype.class));
    verify(cardTypeService, never()).getOrCreate(any(CardType.class));
    verify(cardWeaknessService, never()).getOrCreate(any(CardWeakness.class));
  }

  @Test
  void write_shouldHandleExceptionForEachItem_whenMultipleItemsThrowExceptions() throws Exception {
    LinkCardRelationsEntitiesProcessedDTO dto2 = new LinkCardRelationsEntitiesProcessedDTO();
    dto2.setCardAbilities(Arrays.asList(cardAbility));

    Chunk<LinkCardRelationsEntitiesProcessedDTO> chunk = new Chunk<>(Arrays.asList(dto, dto2));

    when(cardAbilityService.getOrCreate(any(CardAbility.class))).thenThrow(new RuntimeException("Service error"));
    when(looterScrapingErrorHandler.formatErrorItem(anyString(), anyString())).thenReturn("Formatted error");
    doNothing().when(looterScrapingErrorHandler).handle(any(), anyString(), anyString(), anyString());

    writer.write(chunk);

    verify(looterScrapingErrorHandler, times(2)).formatErrorItem(
        eq(Constantes.LINK_CARD_RELATIONS_ENTITIES_ITEM), anyString());
    verify(looterScrapingErrorHandler, times(2)).handle(
        any(RuntimeException.class),
        eq(Constantes.LINK_CARD_RELATIONS_ENTITIES_WRITER_CONTEXT),
        eq(Constantes.WRITE_ACTION),
        anyString());
  }
}