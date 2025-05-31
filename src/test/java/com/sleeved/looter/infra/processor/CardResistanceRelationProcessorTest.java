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

import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.entity.atlas.CardResistance;
import com.sleeved.looter.domain.entity.atlas.Resistance;
import com.sleeved.looter.domain.entity.atlas.Type;
import com.sleeved.looter.domain.service.ResistanceService;
import com.sleeved.looter.infra.dto.ResistanceDTO;
import com.sleeved.looter.infra.mapper.CardResistanceMapper;
import com.sleeved.looter.mock.domain.CardMock;
import com.sleeved.looter.mock.domain.CardResistanceMock;
import com.sleeved.looter.mock.domain.ResistanceMock;
import com.sleeved.looter.mock.domain.TypeMock;
import com.sleeved.looter.mock.infra.ResistanceDTOMock;

@ExtendWith(MockitoExtension.class)
class CardResistanceRelationProcessorTest {

  @Mock
  private ResistanceProcessor resistanceProcessor;

  @Mock
  private ResistanceService resistanceService;

  @Mock
  private CardResistanceMapper cardResistanceMapper;

  @InjectMocks
  private CardResistanceRelationProcessor processor;

  private Card card;
  private ResistanceDTO resistanceDTO;
  private Resistance mappedResistance;
  private Resistance foundResistance;
  private CardResistance cardResistance;
  private Type fightingType;

  @BeforeEach
  void setUp() {
    card = CardMock.createBasicMockCard("swsh1-25", "Pikachu");
    fightingType = TypeMock.createMockTypeSavedInDb(1, "Fighting");

    resistanceDTO = ResistanceDTOMock.createMockResistanceDTO("Fighting", "-30");
    mappedResistance = ResistanceMock.createMockResistance(fightingType, "-30");
    foundResistance = ResistanceMock.createMockResistanceSavedInDb(1, fightingType, "-30");
    cardResistance = CardResistanceMock.createMockCardResistanceSavedInDb(1, foundResistance, card);
  }

  @Test
  void process_shouldReturnEmptyList_whenResistanceDTOListIsNull() {
    List<CardResistance> result = processor.process(null, card);

    assertThat(result).isNotNull();
    assertThat(result).isEmpty();

    verify(resistanceProcessor, never()).processFromDTOs(any());
    verify(resistanceService, never()).getByTypeAndValue(any());
    verify(cardResistanceMapper, never()).toEntity(any(), any());
  }

  @Test
  void process_shouldReturnEmptyList_whenResistanceDTOListIsEmpty() {
    List<CardResistance> result = processor.process(Collections.emptyList(), card);

    assertThat(result).isNotNull();
    assertThat(result).isEmpty();

    verify(resistanceProcessor, never()).processFromDTOs(any());
    verify(resistanceService, never()).getByTypeAndValue(any());
    verify(cardResistanceMapper, never()).toEntity(any(), any());
  }

  @Test
  void process_shouldProcessSingleResistanceDTO_whenListContainsOneItem() {
    List<ResistanceDTO> resistanceDTOs = Collections.singletonList(resistanceDTO);
    List<Resistance> mappedResistances = Collections.singletonList(mappedResistance);

    when(resistanceProcessor.processFromDTOs(resistanceDTOs)).thenReturn(mappedResistances);
    when(resistanceService.getByTypeAndValue(mappedResistance)).thenReturn(foundResistance);
    when(cardResistanceMapper.toEntity(foundResistance, card)).thenReturn(cardResistance);

    List<CardResistance> result = processor.process(resistanceDTOs, card);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0)).isEqualTo(cardResistance);
    assertThat(result.get(0).getResistance()).isEqualTo(foundResistance);
    assertThat(result.get(0).getCard()).isEqualTo(card);

    verify(resistanceProcessor).processFromDTOs(resistanceDTOs);
    verify(resistanceService).getByTypeAndValue(mappedResistance);
    verify(cardResistanceMapper).toEntity(foundResistance, card);
  }

  @Test
  void process_shouldProcessMultipleResistanceDTOs_whenListContainsMultipleItems() {
    Type psychicType = TypeMock.createMockTypeSavedInDb(2, "Psychic");

    ResistanceDTO resistanceDTO2 = ResistanceDTOMock.createMockResistanceDTO("Psychic", "-20");
    Resistance mappedResistance2 = ResistanceMock.createMockResistance(psychicType, "-20");
    Resistance foundResistance2 = ResistanceMock.createMockResistanceSavedInDb(2, psychicType, "-20");
    CardResistance cardResistance2 = CardResistanceMock.createMockCardResistanceSavedInDb(2, foundResistance2, card);

    List<ResistanceDTO> resistanceDTOs = Arrays.asList(resistanceDTO, resistanceDTO2);
    List<Resistance> mappedResistances = Arrays.asList(mappedResistance, mappedResistance2);

    when(resistanceProcessor.processFromDTOs(resistanceDTOs)).thenReturn(mappedResistances);
    when(resistanceService.getByTypeAndValue(mappedResistance)).thenReturn(foundResistance);
    when(resistanceService.getByTypeAndValue(mappedResistance2)).thenReturn(foundResistance2);
    when(cardResistanceMapper.toEntity(foundResistance, card)).thenReturn(cardResistance);
    when(cardResistanceMapper.toEntity(foundResistance2, card)).thenReturn(cardResistance2);

    List<CardResistance> result = processor.process(resistanceDTOs, card);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);
    assertThat(result.get(0).getResistance().getType().getLabel()).isEqualTo("Fighting");
    assertThat(result.get(0).getResistance().getValue()).isEqualTo("-30");
    assertThat(result.get(1).getResistance().getType().getLabel()).isEqualTo("Psychic");
    assertThat(result.get(1).getResistance().getValue()).isEqualTo("-20");

    verify(resistanceProcessor).processFromDTOs(resistanceDTOs);
    verify(resistanceService, times(2)).getByTypeAndValue(any(Resistance.class));
    verify(cardResistanceMapper, times(2)).toEntity(any(Resistance.class), any(Card.class));
  }

  @Test
  void process_shouldSkipNullCardResistances_whenMapperReturnsNull() {
    List<ResistanceDTO> resistanceDTOs = Collections.singletonList(resistanceDTO);
    List<Resistance> mappedResistances = Collections.singletonList(mappedResistance);

    when(resistanceProcessor.processFromDTOs(resistanceDTOs)).thenReturn(mappedResistances);
    when(resistanceService.getByTypeAndValue(mappedResistance)).thenReturn(foundResistance);

    List<CardResistance> result = processor.process(resistanceDTOs, card);

    assertThat(result).isNotNull();
    assertThat(result).isEmpty();

    verify(resistanceProcessor).processFromDTOs(resistanceDTOs);
    verify(resistanceService).getByTypeAndValue(mappedResistance);
    verify(cardResistanceMapper).toEntity(foundResistance, card);
  }

  @Test
  void process_shouldHandleMixedResults_whenSomeCardResistancesAreNullAndSomeAreNot() {
    Type psychicType = TypeMock.createMockTypeSavedInDb(2, "Psychic");

    ResistanceDTO resistanceDTO1 = ResistanceDTOMock.createMockResistanceDTO("Fighting", "-30");
    ResistanceDTO resistanceDTO2 = ResistanceDTOMock.createMockResistanceDTO("Psychic", "-20");

    Resistance mappedResistance1 = ResistanceMock.createMockResistance(fightingType, "-30");
    Resistance mappedResistance2 = ResistanceMock.createMockResistance(psychicType, "-20");

    Resistance foundResistance1 = ResistanceMock.createMockResistanceSavedInDb(1, fightingType, "-30");
    Resistance foundResistance2 = ResistanceMock.createMockResistanceSavedInDb(2, psychicType, "-20");

    CardResistance cardResistance1 = CardResistanceMock.createMockCardResistanceSavedInDb(1, foundResistance1, card);

    List<ResistanceDTO> resistanceDTOs = Arrays.asList(resistanceDTO1, resistanceDTO2);
    List<Resistance> mappedResistances = Arrays.asList(mappedResistance1, mappedResistance2);

    when(resistanceProcessor.processFromDTOs(resistanceDTOs)).thenReturn(mappedResistances);
    when(resistanceService.getByTypeAndValue(mappedResistance1)).thenReturn(foundResistance1);
    when(resistanceService.getByTypeAndValue(mappedResistance2)).thenReturn(foundResistance2);
    when(cardResistanceMapper.toEntity(foundResistance1, card)).thenReturn(cardResistance1);

    List<CardResistance> result = processor.process(resistanceDTOs, card);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getResistance().getType().getLabel()).isEqualTo("Fighting");
    assertThat(result.get(0).getResistance().getValue()).isEqualTo("-30");

    verify(resistanceProcessor).processFromDTOs(resistanceDTOs);
    verify(resistanceService, times(2)).getByTypeAndValue(any(Resistance.class));
    verify(cardResistanceMapper, times(2)).toEntity(any(Resistance.class), any(Card.class));
  }
}