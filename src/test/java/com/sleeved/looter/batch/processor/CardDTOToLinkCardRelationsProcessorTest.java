package com.sleeved.looter.batch.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
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
import com.sleeved.looter.domain.service.CardService;
import com.sleeved.looter.infra.dto.CardDTO;
import com.sleeved.looter.infra.dto.LinkCardRelationsEntitiesProcessedDTO;
import com.sleeved.looter.infra.processor.CardAbilityRelationProcessor;
import com.sleeved.looter.infra.processor.CardAttackRelationProcessor;
import com.sleeved.looter.infra.processor.CardResistanceRelationProcessor;
import com.sleeved.looter.infra.processor.CardSubtypeRelationProcessor;
import com.sleeved.looter.infra.processor.CardTypeRelationProcessor;
import com.sleeved.looter.infra.processor.CardWeaknessRelationProcessor;
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
import com.sleeved.looter.mock.infra.AbilityDTOMock;
import com.sleeved.looter.mock.infra.AttackDTOMock;
import com.sleeved.looter.mock.infra.CardDTOMock;
import com.sleeved.looter.mock.infra.ResistanceDTOMock;
import com.sleeved.looter.mock.infra.WeaknessDTOMock;

@ExtendWith(MockitoExtension.class)
class CardDTOToLinkCardRelationsProcessorTest {

  @Mock
  private CardService cardService;

  @Mock
  private LooterScrapingErrorHandler looterScrapingErrorHandler;

  @Mock
  private CardAbilityRelationProcessor cardAbilityRelationProcessor;

  @Mock
  private CardAttackRelationProcessor cardAttackRelationProcessor;

  @Mock
  private CardResistanceRelationProcessor cardResistanceRelationProcessor;

  @Mock
  private CardSubtypeRelationProcessor cardSubtypeRelationProcessor;

  @Mock
  private CardTypeRelationProcessor cardTypeRelationProcessor;

  @Mock
  private CardWeaknessRelationProcessor cardWeaknessRelationProcessor;

  @InjectMocks
  private CardDTOToLinkCardRelationsProcessor processor;

  private CardDTO cardDTO;
  private Card card;
  private List<CardAbility> mockCardAbilities;
  private List<CardAttack> mockCardAttacks;
  private List<CardResistance> mockCardResistances;
  private List<CardSubtype> mockCardSubtypes;
  private List<CardType> mockCardTypes;
  private List<CardWeakness> mockCardWeaknesses;

  @BeforeEach
  void setUp() {
    cardDTO = CardDTOMock.createMockCardDTO("swsh1-25", "Pikachu");
    cardDTO.setAbilities(Collections.singletonList(
        AbilityDTOMock.createMockAbilityDTO("Thunder Shock",
            "Once during your turn, you may have your opponent switch their Active Pokémon.",
            "Ability")));
    cardDTO.setAttacks(Collections.singletonList(
        AttackDTOMock.createMockAttackDTO("Thunderbolt",
            Arrays.asList("Colorless", "Lightning", "Lightning"),
            3, "120",
            "Discard all Energy attached to this Pokémon.")));
    cardDTO.setResistances(Collections.singletonList(
        ResistanceDTOMock.createMockResistanceDTO("Fighting", "-30")));
    cardDTO.setSubtypes(Collections.singletonList("Basic"));
    cardDTO.setTypes(Collections.singletonList("Lightning"));
    cardDTO.setWeaknesses(Collections.singletonList(
        WeaknessDTOMock.createMockWeaknessDTO("Fighting", "×2")));

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

    CardAbility cardAbility = CardAbilityMock.createMockCardAbilitySavedInDb(1, ability, card);
    CardAttack cardAttack = CardAttackMock.createMockCardAttackSavedInDb(1, attack, card);
    CardResistance cardResistance = CardResistanceMock.createMockCardResistanceSavedInDb(1, resistance, card);
    CardSubtype cardSubtype = CardSubtypeMock.createMockCardSubtypeSavedInDb(1, subtype, card);
    CardType cardType = CardTypeMock.createMockCardTypeSavedInDb(1, electricType, card);
    CardWeakness cardWeakness = CardWeaknessMock.createMockCardWeaknessSavedInDb(1, weakness, card);

    mockCardAbilities = Arrays.asList(cardAbility);
    mockCardAttacks = Arrays.asList(cardAttack);
    mockCardResistances = Arrays.asList(cardResistance);
    mockCardSubtypes = Arrays.asList(cardSubtype);
    mockCardTypes = Arrays.asList(cardType);
    mockCardWeaknesses = Arrays.asList(cardWeakness);
  }

  @Test
  void process_shouldReturnLinkCardRelationsEntitiesProcessedDTO_whenProcessingSucceeds() {
    when(cardService.getById(cardDTO.getId())).thenReturn(card);

    when(cardAbilityRelationProcessor.process(anyList(), eq(card))).thenReturn(mockCardAbilities);
    when(cardAttackRelationProcessor.process(anyList(), eq(card))).thenReturn(mockCardAttacks);
    when(cardResistanceRelationProcessor.process(anyList(), eq(card))).thenReturn(mockCardResistances);
    when(cardSubtypeRelationProcessor.process(anyList(), eq(card))).thenReturn(mockCardSubtypes);
    when(cardTypeRelationProcessor.process(anyList(), eq(card))).thenReturn(mockCardTypes);
    when(cardWeaknessRelationProcessor.process(anyList(), eq(card))).thenReturn(mockCardWeaknesses);

    LinkCardRelationsEntitiesProcessedDTO result = processor.process(cardDTO);

    assertThat(result).isNotNull();
    assertThat(result.getCardAbilities()).isEqualTo(mockCardAbilities);
    assertThat(result.getCardAttacks()).isEqualTo(mockCardAttacks);
    assertThat(result.getCardResistances()).isEqualTo(mockCardResistances);
    assertThat(result.getCardSubtypes()).isEqualTo(mockCardSubtypes);
    assertThat(result.getCardTypes()).isEqualTo(mockCardTypes);
    assertThat(result.getCardWeaknesses()).isEqualTo(mockCardWeaknesses);

    verify(cardService).getById(cardDTO.getId());
    verify(cardAbilityRelationProcessor).process(cardDTO.getAbilities(), card);
    verify(cardAttackRelationProcessor).process(cardDTO.getAttacks(), card);
    verify(cardResistanceRelationProcessor).process(cardDTO.getResistances(), card);
    verify(cardSubtypeRelationProcessor).process(cardDTO.getSubtypes(), card);
    verify(cardTypeRelationProcessor).process(cardDTO.getTypes(), card);
    verify(cardWeaknessRelationProcessor).process(cardDTO.getWeaknesses(), card);
  }

  @Test
  void process_shouldHandleEmptyLists() {
    CardDTO emptyCardDTO = new CardDTO();
    emptyCardDTO.setId("swsh1-25");
    emptyCardDTO.setAbilities(Collections.emptyList());
    emptyCardDTO.setAttacks(Collections.emptyList());
    emptyCardDTO.setResistances(Collections.emptyList());
    emptyCardDTO.setSubtypes(Collections.emptyList());
    emptyCardDTO.setTypes(Collections.emptyList());
    emptyCardDTO.setWeaknesses(Collections.emptyList());

    when(cardService.getById(emptyCardDTO.getId())).thenReturn(card);

    when(cardAbilityRelationProcessor.process(Collections.emptyList(), card)).thenReturn(Collections.emptyList());
    when(cardAttackRelationProcessor.process(Collections.emptyList(), card)).thenReturn(Collections.emptyList());
    when(cardResistanceRelationProcessor.process(Collections.emptyList(), card)).thenReturn(Collections.emptyList());
    when(cardSubtypeRelationProcessor.process(Collections.emptyList(), card)).thenReturn(Collections.emptyList());
    when(cardTypeRelationProcessor.process(Collections.emptyList(), card)).thenReturn(Collections.emptyList());
    when(cardWeaknessRelationProcessor.process(Collections.emptyList(), card)).thenReturn(Collections.emptyList());

    LinkCardRelationsEntitiesProcessedDTO result = processor.process(emptyCardDTO);

    assertThat(result).isNotNull();
    assertThat(result.getCardAbilities()).isEmpty();
    assertThat(result.getCardAttacks()).isEmpty();
    assertThat(result.getCardResistances()).isEmpty();
    assertThat(result.getCardSubtypes()).isEmpty();
    assertThat(result.getCardTypes()).isEmpty();
    assertThat(result.getCardWeaknesses()).isEmpty();
  }

  @Test
  void process_shouldReturnNull_whenExceptionIsThrown() {
    when(cardService.getById(cardDTO.getId())).thenThrow(new RuntimeException("Card not found"));
    when(looterScrapingErrorHandler.formatErrorItem(anyString(), anyString())).thenReturn("Formatted error");
    doNothing().when(looterScrapingErrorHandler).handle(any(), anyString(), anyString(), anyString());

    LinkCardRelationsEntitiesProcessedDTO result = processor.process(cardDTO);

    assertThat(result).isNull();

    verify(looterScrapingErrorHandler).formatErrorItem(eq(Constantes.CARD_DTO_ITEM), anyString());
    verify(looterScrapingErrorHandler).handle(
        any(RuntimeException.class),
        eq(Constantes.CARD_DTO_TO_LINK_CARD_RELATIONS_ENTITIES_PROCESSOR_CONTEXT),
        eq(Constantes.PROCESSOR_ACTION),
        anyString());
  }

  @Test
  void process_shouldHandlePartialProcessingFailures() {
    when(cardService.getById(cardDTO.getId())).thenReturn(card);

    when(cardAbilityRelationProcessor.process(anyList(), eq(card))).thenReturn(mockCardAbilities);
    when(cardAttackRelationProcessor.process(anyList(), eq(card))).thenReturn(mockCardAttacks);
    when(cardResistanceRelationProcessor.process(anyList(), eq(card)))
        .thenThrow(new RuntimeException("Processing failed"));

    when(looterScrapingErrorHandler.formatErrorItem(anyString(), anyString())).thenReturn("Formatted error");
    doNothing().when(looterScrapingErrorHandler).handle(any(), anyString(), anyString(), anyString());

    LinkCardRelationsEntitiesProcessedDTO result = processor.process(cardDTO);

    assertThat(result).isNull();

    verify(looterScrapingErrorHandler).formatErrorItem(eq(Constantes.CARD_DTO_ITEM), anyString());
    verify(looterScrapingErrorHandler).handle(
        any(RuntimeException.class),
        eq(Constantes.CARD_DTO_TO_LINK_CARD_RELATIONS_ENTITIES_PROCESSOR_CONTEXT),
        eq(Constantes.PROCESSOR_ACTION),
        anyString());
  }
}