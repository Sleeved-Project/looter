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
import com.sleeved.looter.domain.entity.atlas.CardWeakness;
import com.sleeved.looter.domain.entity.atlas.Type;
import com.sleeved.looter.domain.entity.atlas.Weakness;
import com.sleeved.looter.domain.service.WeaknessService;
import com.sleeved.looter.infra.dto.WeaknessDTO;
import com.sleeved.looter.infra.mapper.CardWeaknessMapper;
import com.sleeved.looter.mock.domain.CardMock;
import com.sleeved.looter.mock.domain.CardWeaknessMock;
import com.sleeved.looter.mock.domain.TypeMock;
import com.sleeved.looter.mock.domain.WeaknessMock;
import com.sleeved.looter.mock.infra.WeaknessDTOMock;

@ExtendWith(MockitoExtension.class)
class CardWeaknessRelationProcessorTest {

  @Mock
  private WeaknessProcessor weaknessProcessor;

  @Mock
  private WeaknessService weaknessService;

  @Mock
  private CardWeaknessMapper cardWeaknessMapper;

  @InjectMocks
  private CardWeaknessRelationProcessor processor;

  private Card card;
  private WeaknessDTO weaknessDTO;
  private Weakness mappedWeakness;
  private Weakness foundWeakness;
  private CardWeakness cardWeakness;
  private Type fireType;

  @BeforeEach
  void setUp() {
    card = CardMock.createBasicMockCard("swsh1-25", "Pikachu");
    fireType = TypeMock.createMockTypeSavedInDb(1, "Fire");

    weaknessDTO = WeaknessDTOMock.createMockWeaknessDTO("Fire", "×2");
    mappedWeakness = WeaknessMock.createMockWeakness(fireType, "×2");
    foundWeakness = WeaknessMock.createMockWeaknessSavedInDb(1, fireType, "×2");
    cardWeakness = CardWeaknessMock.createMockCardWeaknessSavedInDb(1, foundWeakness, card);
  }

  @Test
  void process_shouldReturnEmptyList_whenWeaknessDTOListIsNull() {
    List<CardWeakness> result = processor.process(null, card);

    assertThat(result).isNotNull();
    assertThat(result).isEmpty();

    verify(weaknessProcessor, never()).processFromDTOs(any());
    verify(weaknessService, never()).getByTypeAndValue(any());
    verify(cardWeaknessMapper, never()).toEntity(any(), any());
  }

  @Test
  void process_shouldReturnEmptyList_whenWeaknessDTOListIsEmpty() {
    List<CardWeakness> result = processor.process(Collections.emptyList(), card);

    assertThat(result).isNotNull();
    assertThat(result).isEmpty();

    verify(weaknessProcessor, never()).processFromDTOs(any());
    verify(weaknessService, never()).getByTypeAndValue(any());
    verify(cardWeaknessMapper, never()).toEntity(any(), any());
  }

  @Test
  void process_shouldProcessSingleWeaknessDTO_whenListContainsOneItem() {
    List<WeaknessDTO> weaknessDTOs = Collections.singletonList(weaknessDTO);
    List<Weakness> mappedWeaknesses = Collections.singletonList(mappedWeakness);

    when(weaknessProcessor.processFromDTOs(weaknessDTOs)).thenReturn(mappedWeaknesses);
    when(weaknessService.getByTypeAndValue(mappedWeakness)).thenReturn(foundWeakness);
    when(cardWeaknessMapper.toEntity(foundWeakness, card)).thenReturn(cardWeakness);

    List<CardWeakness> result = processor.process(weaknessDTOs, card);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0)).isEqualTo(cardWeakness);
    assertThat(result.get(0).getWeakness()).isEqualTo(foundWeakness);
    assertThat(result.get(0).getCard()).isEqualTo(card);

    verify(weaknessProcessor).processFromDTOs(weaknessDTOs);
    verify(weaknessService).getByTypeAndValue(mappedWeakness);
    verify(cardWeaknessMapper).toEntity(foundWeakness, card);
  }

  @Test
  void process_shouldProcessMultipleWeaknessDTOs_whenListContainsMultipleItems() {
    Type waterType = TypeMock.createMockTypeSavedInDb(2, "Water");

    WeaknessDTO weaknessDTO2 = WeaknessDTOMock.createMockWeaknessDTO("Water", "×2");
    Weakness mappedWeakness2 = WeaknessMock.createMockWeakness(waterType, "×2");
    Weakness foundWeakness2 = WeaknessMock.createMockWeaknessSavedInDb(2, waterType, "×2");
    CardWeakness cardWeakness2 = CardWeaknessMock.createMockCardWeaknessSavedInDb(2, foundWeakness2, card);

    List<WeaknessDTO> weaknessDTOs = Arrays.asList(weaknessDTO, weaknessDTO2);
    List<Weakness> mappedWeaknesses = Arrays.asList(mappedWeakness, mappedWeakness2);

    when(weaknessProcessor.processFromDTOs(weaknessDTOs)).thenReturn(mappedWeaknesses);
    when(weaknessService.getByTypeAndValue(mappedWeakness)).thenReturn(foundWeakness);
    when(weaknessService.getByTypeAndValue(mappedWeakness2)).thenReturn(foundWeakness2);
    when(cardWeaknessMapper.toEntity(foundWeakness, card)).thenReturn(cardWeakness);
    when(cardWeaknessMapper.toEntity(foundWeakness2, card)).thenReturn(cardWeakness2);

    List<CardWeakness> result = processor.process(weaknessDTOs, card);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);
    assertThat(result.get(0).getWeakness().getType().getLabel()).isEqualTo("Fire");
    assertThat(result.get(0).getWeakness().getValue()).isEqualTo("×2");
    assertThat(result.get(1).getWeakness().getType().getLabel()).isEqualTo("Water");
    assertThat(result.get(1).getWeakness().getValue()).isEqualTo("×2");

    verify(weaknessProcessor).processFromDTOs(weaknessDTOs);
    verify(weaknessService, times(2)).getByTypeAndValue(any(Weakness.class));
    verify(cardWeaknessMapper, times(2)).toEntity(any(Weakness.class), any(Card.class));
  }

  @Test
  void process_shouldSkipNullCardWeaknesses_whenMapperReturnsNull() {
    List<WeaknessDTO> weaknessDTOs = Collections.singletonList(weaknessDTO);
    List<Weakness> mappedWeaknesses = Collections.singletonList(mappedWeakness);

    when(weaknessProcessor.processFromDTOs(weaknessDTOs)).thenReturn(mappedWeaknesses);
    when(weaknessService.getByTypeAndValue(mappedWeakness)).thenReturn(foundWeakness);

    List<CardWeakness> result = processor.process(weaknessDTOs, card);

    assertThat(result).isNotNull();
    assertThat(result).isEmpty();

    verify(weaknessProcessor).processFromDTOs(weaknessDTOs);
    verify(weaknessService).getByTypeAndValue(mappedWeakness);
    verify(cardWeaknessMapper).toEntity(foundWeakness, card);
  }

  @Test
  void process_shouldHandleDifferentWeaknessValues() {
    Type waterType = TypeMock.createMockTypeSavedInDb(2, "Water");

    WeaknessDTO weaknessDTO1 = WeaknessDTOMock.createMockWeaknessDTO("Fire", "×2");
    WeaknessDTO weaknessDTO2 = WeaknessDTOMock.createMockWeaknessDTO("Water", "×3");

    Weakness mappedWeakness1 = WeaknessMock.createMockWeakness(fireType, "×2");
    Weakness mappedWeakness2 = WeaknessMock.createMockWeakness(waterType, "×3");

    Weakness foundWeakness1 = WeaknessMock.createMockWeaknessSavedInDb(1, fireType, "×2");
    Weakness foundWeakness2 = WeaknessMock.createMockWeaknessSavedInDb(2, waterType, "×3");

    CardWeakness cardWeakness1 = CardWeaknessMock.createMockCardWeaknessSavedInDb(1, foundWeakness1, card);
    CardWeakness cardWeakness2 = CardWeaknessMock.createMockCardWeaknessSavedInDb(2, foundWeakness2, card);

    List<WeaknessDTO> weaknessDTOs = Arrays.asList(weaknessDTO1, weaknessDTO2);
    List<Weakness> mappedWeaknesses = Arrays.asList(mappedWeakness1, mappedWeakness2);

    when(weaknessProcessor.processFromDTOs(weaknessDTOs)).thenReturn(mappedWeaknesses);
    when(weaknessService.getByTypeAndValue(mappedWeakness1)).thenReturn(foundWeakness1);
    when(weaknessService.getByTypeAndValue(mappedWeakness2)).thenReturn(foundWeakness2);
    when(cardWeaknessMapper.toEntity(foundWeakness1, card)).thenReturn(cardWeakness1);
    when(cardWeaknessMapper.toEntity(foundWeakness2, card)).thenReturn(cardWeakness2);

    List<CardWeakness> result = processor.process(weaknessDTOs, card);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);
    assertThat(result.get(0).getWeakness().getValue()).isEqualTo("×2");
    assertThat(result.get(1).getWeakness().getValue()).isEqualTo("×3");

    verify(weaknessProcessor).processFromDTOs(weaknessDTOs);
    verify(weaknessService, times(2)).getByTypeAndValue(any(Weakness.class));
    verify(cardWeaknessMapper, times(2)).toEntity(any(Weakness.class), any(Card.class));
  }
}