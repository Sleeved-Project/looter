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
import com.sleeved.looter.domain.entity.atlas.CardSubtype;
import com.sleeved.looter.domain.entity.atlas.Subtype;
import com.sleeved.looter.domain.service.SubtypeService;
import com.sleeved.looter.infra.mapper.CardSubtypeMapper;
import com.sleeved.looter.infra.mapper.SubtypeMapper;
import com.sleeved.looter.mock.domain.CardMock;
import com.sleeved.looter.mock.domain.CardSubtypeMock;
import com.sleeved.looter.mock.domain.SubtypeMock;

@ExtendWith(MockitoExtension.class)
class CardSubtypeRelationProcessorTest {

  @Mock
  private SubtypeMapper subtypeMapper;

  @Mock
  private SubtypeService subtypeService;

  @Mock
  private CardSubtypeMapper cardSubtypeMapper;

  @InjectMocks
  private CardSubtypeRelationProcessor processor;

  private Card card;
  private String subtypeDTO;
  private Subtype mappedSubtype;
  private Subtype foundSubtype;
  private CardSubtype cardSubtype;

  @BeforeEach
  void setUp() {
    card = CardMock.createBasicMockCard("swsh1-25", "Pikachu");

    subtypeDTO = "Basic";
    mappedSubtype = SubtypeMock.createMockSubtype("Basic");
    foundSubtype = SubtypeMock.createMockSubtypeSavedInDb(1, "Basic");
    cardSubtype = CardSubtypeMock.createMockCardSubtypeSavedInDb(1, foundSubtype, card);
  }

  @Test
  void process_shouldReturnEmptyList_whenSubtypeDTOListIsNull() {
    List<CardSubtype> result = processor.process(null, card);

    assertThat(result).isNotNull();
    assertThat(result).isEmpty();

    verify(subtypeMapper, never()).toListEntity(any());
    verify(subtypeService, never()).getByLabel(any());
    verify(cardSubtypeMapper, never()).toEntity(any(), any());
  }

  @Test
  void process_shouldReturnEmptyList_whenSubtypeDTOListIsEmpty() {
    List<CardSubtype> result = processor.process(Collections.emptyList(), card);

    assertThat(result).isNotNull();
    assertThat(result).isEmpty();

    verify(subtypeMapper, never()).toListEntity(any());
    verify(subtypeService, never()).getByLabel(any());
    verify(cardSubtypeMapper, never()).toEntity(any(), any());
  }

  @Test
  void process_shouldProcessSingleSubtypeDTO_whenListContainsOneItem() {
    List<String> subtypeDTOs = Collections.singletonList(subtypeDTO);
    List<Subtype> mappedSubtypes = Collections.singletonList(mappedSubtype);

    when(subtypeMapper.toListEntity(subtypeDTOs)).thenReturn(mappedSubtypes);
    when(subtypeService.getByLabel(mappedSubtype.getLabel())).thenReturn(foundSubtype);
    when(cardSubtypeMapper.toEntity(foundSubtype, card)).thenReturn(cardSubtype);

    List<CardSubtype> result = processor.process(subtypeDTOs, card);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0)).isEqualTo(cardSubtype);
    assertThat(result.get(0).getSubtype()).isEqualTo(foundSubtype);
    assertThat(result.get(0).getCard()).isEqualTo(card);

    verify(subtypeMapper).toListEntity(subtypeDTOs);
    verify(subtypeService).getByLabel(mappedSubtype.getLabel());
    verify(cardSubtypeMapper).toEntity(foundSubtype, card);
  }

  @Test
  void process_shouldProcessMultipleSubtypeDTOs_whenListContainsMultipleItems() {
    String subtypeDTO2 = "Stage 1";
    Subtype mappedSubtype2 = SubtypeMock.createMockSubtype("Stage 1");
    Subtype foundSubtype2 = SubtypeMock.createMockSubtypeSavedInDb(2, "Stage 1");
    CardSubtype cardSubtype2 = CardSubtypeMock.createMockCardSubtypeSavedInDb(2, foundSubtype2, card);

    List<String> subtypeDTOs = Arrays.asList(subtypeDTO, subtypeDTO2);
    List<Subtype> mappedSubtypes = Arrays.asList(mappedSubtype, mappedSubtype2);

    when(subtypeMapper.toListEntity(subtypeDTOs)).thenReturn(mappedSubtypes);
    when(subtypeService.getByLabel(mappedSubtype.getLabel())).thenReturn(foundSubtype);
    when(subtypeService.getByLabel(mappedSubtype2.getLabel())).thenReturn(foundSubtype2);
    when(cardSubtypeMapper.toEntity(foundSubtype, card)).thenReturn(cardSubtype);
    when(cardSubtypeMapper.toEntity(foundSubtype2, card)).thenReturn(cardSubtype2);

    List<CardSubtype> result = processor.process(subtypeDTOs, card);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);
    assertThat(result.get(0).getSubtype().getLabel()).isEqualTo("Basic");
    assertThat(result.get(1).getSubtype().getLabel()).isEqualTo("Stage 1");

    verify(subtypeMapper).toListEntity(subtypeDTOs);
    verify(subtypeService).getByLabel(mappedSubtype.getLabel());
    verify(subtypeService).getByLabel(mappedSubtype2.getLabel());
    verify(cardSubtypeMapper).toEntity(foundSubtype, card);
    verify(cardSubtypeMapper).toEntity(foundSubtype2, card);
  }

  @Test
  void process_shouldSkipNull_whenCardSubtypeMapperReturnsNull() {
    List<String> subtypeDTOs = Collections.singletonList(subtypeDTO);
    List<Subtype> mappedSubtypes = Collections.singletonList(mappedSubtype);

    when(subtypeMapper.toListEntity(subtypeDTOs)).thenReturn(mappedSubtypes);
    when(subtypeService.getByLabel(mappedSubtype.getLabel())).thenReturn(foundSubtype);

    List<CardSubtype> result = processor.process(subtypeDTOs, card);

    assertThat(result).isNotNull();
    assertThat(result).isEmpty();

    verify(subtypeMapper).toListEntity(subtypeDTOs);
    verify(subtypeService).getByLabel(mappedSubtype.getLabel());
    verify(cardSubtypeMapper).toEntity(foundSubtype, card);
  }

  @Test
  void process_shouldHandleMixedResults_whenSomeCardSubtypesAreNullAndSomeAreNot() {
    String subtypeDTO1 = "Basic";
    String subtypeDTO2 = "Stage 1";

    Subtype mappedSubtype1 = SubtypeMock.createMockSubtype("Basic");
    Subtype mappedSubtype2 = SubtypeMock.createMockSubtype("Stage 1");

    Subtype foundSubtype1 = SubtypeMock.createMockSubtypeSavedInDb(1, "Basic");
    Subtype foundSubtype2 = SubtypeMock.createMockSubtypeSavedInDb(2, "Stage 1");

    CardSubtype cardSubtype1 = CardSubtypeMock.createMockCardSubtypeSavedInDb(1, foundSubtype1, card);

    List<String> subtypeDTOs = Arrays.asList(subtypeDTO1, subtypeDTO2);
    List<Subtype> mappedSubtypes = Arrays.asList(mappedSubtype1, mappedSubtype2);

    when(subtypeMapper.toListEntity(subtypeDTOs)).thenReturn(mappedSubtypes);
    when(subtypeService.getByLabel(mappedSubtype1.getLabel())).thenReturn(foundSubtype1);
    when(subtypeService.getByLabel(mappedSubtype2.getLabel())).thenReturn(foundSubtype2);
    when(cardSubtypeMapper.toEntity(foundSubtype1, card)).thenReturn(cardSubtype1);

    List<CardSubtype> result = processor.process(subtypeDTOs, card);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getSubtype().getLabel()).isEqualTo("Basic");

    verify(subtypeMapper).toListEntity(subtypeDTOs);
    verify(subtypeService).getByLabel(mappedSubtype1.getLabel());
    verify(subtypeService).getByLabel(mappedSubtype2.getLabel());
    verify(cardSubtypeMapper).toEntity(foundSubtype1, card);
    verify(cardSubtypeMapper).toEntity(foundSubtype2, card);
  }

  @Test
  void process_shouldHandleCommonPokemonSubtypes() {
    List<String> subtypeDTOs = Arrays.asList("Basic", "Stage 1", "Stage 2", "EX", "GX", "VMAX");

    List<Subtype> mappedSubtypes = Arrays.asList(
        SubtypeMock.createMockSubtype("Basic"),
        SubtypeMock.createMockSubtype("Stage 1"),
        SubtypeMock.createMockSubtype("Stage 2"),
        SubtypeMock.createMockSubtype("EX"),
        SubtypeMock.createMockSubtype("GX"),
        SubtypeMock.createMockSubtype("VMAX"));

    List<Subtype> foundSubtypes = Arrays.asList(
        SubtypeMock.createMockSubtypeSavedInDb(1, "Basic"),
        SubtypeMock.createMockSubtypeSavedInDb(2, "Stage 1"),
        SubtypeMock.createMockSubtypeSavedInDb(3, "Stage 2"),
        SubtypeMock.createMockSubtypeSavedInDb(4, "EX"),
        SubtypeMock.createMockSubtypeSavedInDb(5, "GX"),
        SubtypeMock.createMockSubtypeSavedInDb(6, "VMAX"));

    List<CardSubtype> cardSubtypes = Arrays.asList(
        CardSubtypeMock.createMockCardSubtypeSavedInDb(1, foundSubtypes.get(0), card),
        CardSubtypeMock.createMockCardSubtypeSavedInDb(2, foundSubtypes.get(1), card),
        CardSubtypeMock.createMockCardSubtypeSavedInDb(3, foundSubtypes.get(2), card),
        CardSubtypeMock.createMockCardSubtypeSavedInDb(4, foundSubtypes.get(3), card),
        CardSubtypeMock.createMockCardSubtypeSavedInDb(5, foundSubtypes.get(4), card),
        CardSubtypeMock.createMockCardSubtypeSavedInDb(6, foundSubtypes.get(5), card));

    when(subtypeMapper.toListEntity(subtypeDTOs)).thenReturn(mappedSubtypes);

    for (int i = 0; i < mappedSubtypes.size(); i++) {
      when(subtypeService.getByLabel(mappedSubtypes.get(i).getLabel())).thenReturn(foundSubtypes.get(i));
      when(cardSubtypeMapper.toEntity(foundSubtypes.get(i), card)).thenReturn(cardSubtypes.get(i));
    }

    List<CardSubtype> result = processor.process(subtypeDTOs, card);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(6);
    assertThat(result.get(0).getSubtype().getLabel()).isEqualTo("Basic");
    assertThat(result.get(1).getSubtype().getLabel()).isEqualTo("Stage 1");
    assertThat(result.get(2).getSubtype().getLabel()).isEqualTo("Stage 2");
    assertThat(result.get(3).getSubtype().getLabel()).isEqualTo("EX");
    assertThat(result.get(4).getSubtype().getLabel()).isEqualTo("GX");
    assertThat(result.get(5).getSubtype().getLabel()).isEqualTo("VMAX");

    verify(subtypeMapper).toListEntity(subtypeDTOs);
    verify(subtypeService, times(6)).getByLabel(any(String.class));
    verify(cardSubtypeMapper, times(6)).toEntity(any(Subtype.class), any(Card.class));
  }
}