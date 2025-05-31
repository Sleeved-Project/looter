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
import com.sleeved.looter.domain.entity.atlas.CardType;
import com.sleeved.looter.domain.entity.atlas.Type;
import com.sleeved.looter.domain.service.TypeService;
import com.sleeved.looter.infra.mapper.CardTypeMapper;
import com.sleeved.looter.infra.mapper.TypeMapper;
import com.sleeved.looter.mock.domain.CardMock;
import com.sleeved.looter.mock.domain.CardTypeMock;
import com.sleeved.looter.mock.domain.TypeMock;

@ExtendWith(MockitoExtension.class)
class CardTypeRelationProcessorTest {

  @Mock
  private TypeMapper typeMapper;

  @Mock
  private TypeService typeService;

  @Mock
  private CardTypeMapper cardTypeMapper;

  @InjectMocks
  private CardTypeRelationProcessor processor;

  private Card card;
  private String typeDTO;
  private Type mappedType;
  private Type foundType;
  private CardType cardType;

  @BeforeEach
  void setUp() {
    card = CardMock.createBasicMockCard("swsh1-25", "Pikachu");

    typeDTO = "Lightning";
    mappedType = TypeMock.createMockType("Lightning");
    foundType = TypeMock.createMockTypeSavedInDb(1, "Lightning");
    cardType = CardTypeMock.createMockCardTypeSavedInDb(1, foundType, card);
  }

  @Test
  void process_shouldReturnEmptyList_whenTypeDTOListIsNull() {
    List<CardType> result = processor.process(null, card);

    assertThat(result).isNotNull();
    assertThat(result).isEmpty();

    verify(typeMapper, never()).toListEntity(any());
    verify(typeService, never()).getByLabel(any());
    verify(cardTypeMapper, never()).toEntity(any(), any());
  }

  @Test
  void process_shouldReturnEmptyList_whenTypeDTOListIsEmpty() {
    List<CardType> result = processor.process(Collections.emptyList(), card);

    assertThat(result).isNotNull();
    assertThat(result).isEmpty();

    verify(typeMapper, never()).toListEntity(any());
    verify(typeService, never()).getByLabel(any());
    verify(cardTypeMapper, never()).toEntity(any(), any());
  }

  @Test
  void process_shouldProcessSingleTypeDTO_whenListContainsOneItem() {
    List<String> typeDTOs = Collections.singletonList(typeDTO);
    List<Type> mappedTypes = Collections.singletonList(mappedType);

    when(typeMapper.toListEntity(typeDTOs)).thenReturn(mappedTypes);
    when(typeService.getByLabel(mappedType)).thenReturn(foundType);
    when(cardTypeMapper.toEntity(foundType, card)).thenReturn(cardType);

    List<CardType> result = processor.process(typeDTOs, card);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0)).isEqualTo(cardType);
    assertThat(result.get(0).getType()).isEqualTo(foundType);
    assertThat(result.get(0).getCard()).isEqualTo(card);

    verify(typeMapper).toListEntity(typeDTOs);
    verify(typeService).getByLabel(mappedType);
    verify(cardTypeMapper).toEntity(foundType, card);
  }

  @Test
  void process_shouldProcessMultipleTypeDTOs_whenListContainsMultipleItems() {
    String typeDTO2 = "Colorless";
    Type mappedType2 = TypeMock.createMockType("Colorless");
    Type foundType2 = TypeMock.createMockTypeSavedInDb(2, "Colorless");
    CardType cardType2 = CardTypeMock.createMockCardTypeSavedInDb(2, foundType2, card);

    List<String> typeDTOs = Arrays.asList(typeDTO, typeDTO2);
    List<Type> mappedTypes = Arrays.asList(mappedType, mappedType2);

    when(typeMapper.toListEntity(typeDTOs)).thenReturn(mappedTypes);
    when(typeService.getByLabel(mappedType)).thenReturn(foundType);
    when(typeService.getByLabel(mappedType2)).thenReturn(foundType2);
    when(cardTypeMapper.toEntity(foundType, card)).thenReturn(cardType);
    when(cardTypeMapper.toEntity(foundType2, card)).thenReturn(cardType2);

    List<CardType> result = processor.process(typeDTOs, card);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);
    assertThat(result.get(0).getType().getLabel()).isEqualTo("Lightning");
    assertThat(result.get(1).getType().getLabel()).isEqualTo("Colorless");

    verify(typeMapper).toListEntity(typeDTOs);
    verify(typeService, times(2)).getByLabel(any(Type.class));
    verify(cardTypeMapper, times(2)).toEntity(any(Type.class), any(Card.class));
  }

  @Test
  void process_shouldSkipNullCardTypes_whenMapperReturnsNull() {
    List<String> typeDTOs = Collections.singletonList(typeDTO);
    List<Type> mappedTypes = Collections.singletonList(mappedType);

    when(typeMapper.toListEntity(typeDTOs)).thenReturn(mappedTypes);
    when(typeService.getByLabel(mappedType)).thenReturn(foundType);

    List<CardType> result = processor.process(typeDTOs, card);

    assertThat(result).isNotNull();
    assertThat(result).isEmpty();

    verify(typeMapper).toListEntity(typeDTOs);
    verify(typeService).getByLabel(mappedType);
    verify(cardTypeMapper).toEntity(foundType, card);
  }

  @Test
  void process_shouldHandleMixedResults_whenSomeCardTypesAreNullAndSomeAreNot() {
    String typeDTO1 = "Lightning";
    String typeDTO2 = "Colorless";

    Type mappedType1 = TypeMock.createMockType("Lightning");
    Type mappedType2 = TypeMock.createMockType("Colorless");

    Type foundType1 = TypeMock.createMockTypeSavedInDb(1, "Lightning");
    Type foundType2 = TypeMock.createMockTypeSavedInDb(2, "Colorless");

    CardType cardType1 = CardTypeMock.createMockCardTypeSavedInDb(1, foundType1, card);

    List<String> typeDTOs = Arrays.asList(typeDTO1, typeDTO2);
    List<Type> mappedTypes = Arrays.asList(mappedType1, mappedType2);

    when(typeMapper.toListEntity(typeDTOs)).thenReturn(mappedTypes);
    when(typeService.getByLabel(mappedType1)).thenReturn(foundType1);
    when(typeService.getByLabel(mappedType2)).thenReturn(foundType2);
    when(cardTypeMapper.toEntity(foundType1, card)).thenReturn(cardType1);

    List<CardType> result = processor.process(typeDTOs, card);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getType().getLabel()).isEqualTo("Lightning");

    verify(typeMapper).toListEntity(typeDTOs);
    verify(typeService, times(2)).getByLabel(any(Type.class));
    verify(cardTypeMapper, times(2)).toEntity(any(Type.class), any(Card.class));
  }

  @Test
  void process_shouldHandleMultipleCommonPokemonTypes() {
    List<String> typeDTOs = Arrays.asList("Fire", "Water", "Grass", "Lightning", "Psychic", "Fighting");

    List<Type> mappedTypes = Arrays.asList(
        TypeMock.createMockType("Fire"),
        TypeMock.createMockType("Water"),
        TypeMock.createMockType("Grass"),
        TypeMock.createMockType("Lightning"),
        TypeMock.createMockType("Psychic"),
        TypeMock.createMockType("Fighting"));

    List<Type> foundTypes = Arrays.asList(
        TypeMock.createMockTypeSavedInDb(1, "Fire"),
        TypeMock.createMockTypeSavedInDb(2, "Water"),
        TypeMock.createMockTypeSavedInDb(3, "Grass"),
        TypeMock.createMockTypeSavedInDb(4, "Lightning"),
        TypeMock.createMockTypeSavedInDb(5, "Psychic"),
        TypeMock.createMockTypeSavedInDb(6, "Fighting"));

    List<CardType> cardTypes = Arrays.asList(
        CardTypeMock.createMockCardTypeSavedInDb(1, foundTypes.get(0), card),
        CardTypeMock.createMockCardTypeSavedInDb(2, foundTypes.get(1), card),
        CardTypeMock.createMockCardTypeSavedInDb(3, foundTypes.get(2), card),
        CardTypeMock.createMockCardTypeSavedInDb(4, foundTypes.get(3), card),
        CardTypeMock.createMockCardTypeSavedInDb(5, foundTypes.get(4), card),
        CardTypeMock.createMockCardTypeSavedInDb(6, foundTypes.get(5), card));

    when(typeMapper.toListEntity(typeDTOs)).thenReturn(mappedTypes);

    for (int i = 0; i < mappedTypes.size(); i++) {
      when(typeService.getByLabel(mappedTypes.get(i))).thenReturn(foundTypes.get(i));
      when(cardTypeMapper.toEntity(foundTypes.get(i), card)).thenReturn(cardTypes.get(i));
    }

    List<CardType> result = processor.process(typeDTOs, card);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(6);
    assertThat(result.get(0).getType().getLabel()).isEqualTo("Fire");
    assertThat(result.get(1).getType().getLabel()).isEqualTo("Water");
    assertThat(result.get(2).getType().getLabel()).isEqualTo("Grass");
    assertThat(result.get(3).getType().getLabel()).isEqualTo("Lightning");
    assertThat(result.get(4).getType().getLabel()).isEqualTo("Psychic");
    assertThat(result.get(5).getType().getLabel()).isEqualTo("Fighting");

    verify(typeMapper).toListEntity(typeDTOs);
    verify(typeService, times(6)).getByLabel(any(Type.class));
    verify(cardTypeMapper, times(6)).toEntity(any(Type.class), any(Card.class));
  }
}