package com.sleeved.looter.batch.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sleeved.looter.domain.entity.atlas.Attack;
import com.sleeved.looter.domain.entity.atlas.CostAttack;
import com.sleeved.looter.domain.entity.atlas.Type;
import com.sleeved.looter.domain.service.AttackService;
import com.sleeved.looter.domain.service.TypeService;
import com.sleeved.looter.infra.dto.AttackDTO;
import com.sleeved.looter.infra.dto.CardDTO;
import com.sleeved.looter.infra.dto.CostAttackEntitiesProcessedDTO;
import com.sleeved.looter.infra.mapper.AttackMapper;
import com.sleeved.looter.infra.mapper.CostAttackMapper;
import com.sleeved.looter.infra.mapper.TypeMapper;
import com.sleeved.looter.infra.service.LooterScrapingErrorHandler;
import com.sleeved.looter.mock.domain.AttackMock;
import com.sleeved.looter.mock.domain.CostAttackMock;
import com.sleeved.looter.mock.domain.TypeMock;
import com.sleeved.looter.mock.infra.AttackDTOMock;
import com.sleeved.looter.mock.infra.CardDTOMock;

@ExtendWith(MockitoExtension.class)
class CardDTOToCostAttackCardProcessorTest {

  @Mock
  private AttackMapper attackMapper;

  @Mock
  private AttackService attackService;

  @Mock
  private TypeMapper typeMapper;

  @Mock
  private TypeService typeService;

  @Mock
  private CostAttackMapper costAttackMapper;

  @Mock
  private LooterScrapingErrorHandler looterScrapingErrorHandler;

  @InjectMocks
  private CardDTOToCostAttackCardProcessor processor;

  @Captor
  private ArgumentCaptor<String> errorMessageCaptor;

  @Test
  void process_shouldReturnCorrectDTO_whenValidInput() {
    CardDTO cardDTO = CardDTOMock.createMockCardDTO("base1-1", "Charizard");
    AttackDTO attackDTO = AttackDTOMock.createMockAttackDTO("Fire Spin", Arrays.asList("Fire", "Fire"), 2, "100", null);
    cardDTO.setAttacks(Arrays.asList(attackDTO));

    Attack attack = AttackMock.createMockAttackSavedInDb(1, "Fire Spin", "100", 2, null);
    Type fireType = TypeMock.createMockTypeSavedInDb(1, "Fire");
    CostAttack costAttack = CostAttackMock.createMockCostAttackSavedInDb(1, attack, fireType, 2, false);

    when(attackMapper.toEntity(attackDTO)).thenReturn(attack);
    when(attackService.getByNameAndDamageAndConvertedEnegyCostAndText(attack)).thenReturn(attack);
    when(typeMapper.toListEntity(attackDTO.getCost())).thenReturn(Arrays.asList(fireType, fireType));
    when(typeService.getByLabel(fireType)).thenReturn(fireType);
    when(costAttackMapper.toEntity(any(Attack.class), any(Type.class), any(Integer.class))).thenReturn(costAttack);

    CostAttackEntitiesProcessedDTO result = processor.process(cardDTO);

    assertThat(result).isNotNull();
    assertThat(result.getCostAttacks()).hasSize(1);
    verify(attackMapper).toEntity(attackDTO);
    verify(attackService).getByNameAndDamageAndConvertedEnegyCostAndText(attack);
    verify(typeMapper).toListEntity(attackDTO.getCost());
    verify(typeService, times(2)).getByLabel(fireType);
    verify(costAttackMapper).toEntity(attack, fireType, 2);
  }

  @Test
  void process_shouldReturnNullAndLogError_whenExceptionOccurs() {
    CardDTO cardDTO = CardDTOMock.createMockCardDTO("base1-1", "Charizard");
    AttackDTO attackDTO = AttackDTOMock.createMockAttackDTO("Fire Spin", Arrays.asList("Fire"), 1, "100", null);
    cardDTO.setAttacks(Arrays.asList(attackDTO));

    when(attackMapper.toEntity(attackDTO)).thenThrow(new RuntimeException("Test exception"));
    doNothing().when(looterScrapingErrorHandler).handle(
        any(Exception.class), anyString(), anyString(), anyString());
    when(looterScrapingErrorHandler.formatErrorItem(anyString(), anyString()))
        .thenReturn("Formatted error item");

    CostAttackEntitiesProcessedDTO result = processor.process(cardDTO);

    assertThat(result).isNull();
    verify(looterScrapingErrorHandler).handle(
        any(Exception.class),
        anyString(),
        anyString(),
        anyString());
  }

  @Test
  void process_shouldHandleCardWithNoAttacks() {
    CardDTO cardDTO = CardDTOMock.createMockCardDTO("base1-1", "Charizard");
    cardDTO.setAttacks(null);

    CostAttackEntitiesProcessedDTO result = processor.process(cardDTO);

    assertThat(result).isNotNull();
    assertThat(result.getCostAttacks()).isEmpty();
    verify(attackMapper, never()).toEntity(any(AttackDTO.class));
  }

  @Test
  void processCostAttacks_shouldReturnEmptyList_whenAttacksIsNull() {
    List<CostAttack> result = processor.processCostAttacks(null);

    assertThat(result).isNotNull().isEmpty();
  }

  @Test
  void processCostAttacks_shouldReturnEmptyList_whenAttacksIsEmpty() {
    List<CostAttack> result = processor.processCostAttacks(Collections.emptyList());

    assertThat(result).isNotNull().isEmpty();
  }

  @Test
  void processCostAttacks_shouldProcessFreeAttacks() {
    AttackDTO attackDTO = AttackDTOMock.createMockAttackDTO("Free Attack", Collections.singletonList("Free"), 0, "20",
        "Free attack text");
    Attack attack = AttackMock.createMockAttackSavedInDb(1, "Free Attack", "20", 0, "Free attack text");
    CostAttack freeCostAttack = CostAttackMock.createMockFreeCostAttackSavedInDb(1, attack);

    when(attackMapper.toEntity(attackDTO)).thenReturn(attack);
    when(attackService.getByNameAndDamageAndConvertedEnegyCostAndText(attack)).thenReturn(attack);
    when(costAttackMapper.toFreeAttackEntity(attack)).thenReturn(freeCostAttack);

    List<CostAttack> result = processor.processCostAttacks(Collections.singletonList(attackDTO));

    assertThat(result).hasSize(1);
    assertThat(result.get(0)).isEqualTo(freeCostAttack);
    verify(costAttackMapper).toFreeAttackEntity(attack);
    verify(typeMapper, never()).toListEntity(any());
  }

  @Test
  void processCostAttacks_shouldProcessNonFreeAttacks() {
    AttackDTO attackDTO = AttackDTOMock.createMockAttackDTO("Water Gun", Arrays.asList("Water", "Colorless"), 2, "40",
        null);
    Attack attack = AttackMock.createMockAttackSavedInDb(2, "Water Gun", "40", 2, null);
    Type waterType = TypeMock.createMockTypeSavedInDb(2, "Water");
    Type colorlessType = TypeMock.createMockTypeSavedInDb(3, "Colorless");
    CostAttack waterCostAttack = CostAttackMock.createMockCostAttackSavedInDb(2, attack, waterType, 1, false);
    CostAttack colorlessCostAttack = CostAttackMock.createMockCostAttackSavedInDb(3, attack, colorlessType, 1, false);

    when(attackMapper.toEntity(attackDTO)).thenReturn(attack);
    when(attackService.getByNameAndDamageAndConvertedEnegyCostAndText(attack)).thenReturn(attack);
    when(typeMapper.toListEntity(attackDTO.getCost())).thenReturn(Arrays.asList(waterType, colorlessType));
    when(typeService.getByLabel(waterType)).thenReturn(waterType);
    when(typeService.getByLabel(colorlessType)).thenReturn(colorlessType);
    when(costAttackMapper.toEntity(attack, waterType, 1)).thenReturn(waterCostAttack);
    when(costAttackMapper.toEntity(attack, colorlessType, 1)).thenReturn(colorlessCostAttack);

    List<CostAttack> result = processor.processCostAttacks(Collections.singletonList(attackDTO));

    assertThat(result).hasSize(2);
    assertThat(result).containsExactlyInAnyOrder(waterCostAttack, colorlessCostAttack);
    verify(typeMapper).toListEntity(attackDTO.getCost());
    verify(typeService).getByLabel(waterType);
    verify(typeService).getByLabel(colorlessType);
    verify(costAttackMapper).toEntity(attack, waterType, 1);
    verify(costAttackMapper).toEntity(attack, colorlessType, 1);
  }

  @Test
  void processCostAttacks_shouldHandleMixOfFreeAndNonFreeAttacks() {
    AttackDTO freeAttackDTO = AttackDTOMock.createMockAttackDTO("Free Attack", Collections.singletonList("Free"), 0,
        "10", null);
    AttackDTO normalAttackDTO = AttackDTOMock.createMockAttackDTO("Normal Attack", Arrays.asList("Fire"), 1, "30",
        null);

    Attack freeAttack = AttackMock.createMockAttackSavedInDb(1, "Free Attack", "10", 0, null);
    Attack normalAttack = AttackMock.createMockAttackSavedInDb(2, "Normal Attack", "30", 1, null);

    Type fireType = TypeMock.createMockTypeSavedInDb(1, "Fire");

    CostAttack freeCostAttack = CostAttackMock.createMockFreeCostAttackSavedInDb(1, freeAttack);
    CostAttack normalCostAttack = CostAttackMock.createMockCostAttackSavedInDb(2, normalAttack, fireType, 1, false);

    when(attackMapper.toEntity(freeAttackDTO)).thenReturn(freeAttack);
    when(attackMapper.toEntity(normalAttackDTO)).thenReturn(normalAttack);
    when(attackService.getByNameAndDamageAndConvertedEnegyCostAndText(freeAttack)).thenReturn(freeAttack);
    when(attackService.getByNameAndDamageAndConvertedEnegyCostAndText(normalAttack)).thenReturn(normalAttack);
    when(costAttackMapper.toFreeAttackEntity(freeAttack)).thenReturn(freeCostAttack);
    when(typeMapper.toListEntity(normalAttackDTO.getCost())).thenReturn(Arrays.asList(fireType));
    when(typeService.getByLabel(fireType)).thenReturn(fireType);
    when(costAttackMapper.toEntity(normalAttack, fireType, 1)).thenReturn(normalCostAttack);

    List<CostAttack> result = processor.processCostAttacks(Arrays.asList(freeAttackDTO, normalAttackDTO));

    assertThat(result).hasSize(2);
    assertThat(result).containsExactlyInAnyOrder(freeCostAttack, normalCostAttack);
    verify(costAttackMapper).toFreeAttackEntity(freeAttack);
    verify(typeMapper).toListEntity(normalAttackDTO.getCost());
  }

  @Test
  void isAttackFree_shouldReturnTrue_whenConvertedEnergyCostIsZero() {
    Attack attack = AttackMock.createMockAttack("Free Attack", "10", 0, null);

    boolean result = processor.isAttackFree(attack);

    assertThat(result).isTrue();
  }

  @Test
  void isAttackFree_shouldReturnFalse_whenConvertedEnergyCostIsNotZero() {
    Attack attack = AttackMock.createMockAttack("Normal Attack", "30", 2, null);

    boolean result = processor.isAttackFree(attack);

    assertThat(result).isFalse();
  }

  @Test
  void findAttackFromDTO_shouldReturnCorrectAttack() {
    AttackDTO attackDTO = AttackDTOMock.createMockAttackDTO("Thunderbolt", Arrays.asList("Lightning", "Lightning"), 2,
        "90", "Discard all Energy");
    Attack mappedAttack = AttackMock.createMockAttack("Thunderbolt", "90", 2, "Discard all Energy");
    Attack foundAttack = AttackMock.createMockAttackSavedInDb(5, "Thunderbolt", "90", 2, "Discard all Energy");

    when(attackMapper.toEntity(attackDTO)).thenReturn(mappedAttack);
    when(attackService.getByNameAndDamageAndConvertedEnegyCostAndText(mappedAttack)).thenReturn(foundAttack);

    Attack result = processor.findAttackFromDTO(attackDTO);

    assertThat(result).isEqualTo(foundAttack);
    verify(attackMapper).toEntity(attackDTO);
    verify(attackService).getByNameAndDamageAndConvertedEnegyCostAndText(mappedAttack);
  }

  @Test
  void findAndAggregateTypeCosts_shouldAggregateSameTypes() {
    AttackDTO attackDTO = AttackDTOMock.createMockAttackDTO("Double Fire", Arrays.asList("Fire", "Fire"), 2, "60",
        null);
    Type fireType = TypeMock.createMockTypeSavedInDb(1, "Fire");

    when(typeMapper.toListEntity(attackDTO.getCost())).thenReturn(Arrays.asList(fireType, fireType));
    when(typeService.getByLabel(fireType)).thenReturn(fireType);

    Map<Type, Integer> result = processor.findAndAggregateTypeCosts(attackDTO);

    assertThat(result).hasSize(1);
    assertThat(result).containsEntry(fireType, 2);
    verify(typeMapper).toListEntity(attackDTO.getCost());
    verify(typeService, times(2)).getByLabel(fireType);
  }

  @Test
  void findAndAggregateTypeCosts_shouldHandleDifferentTypes() {
    AttackDTO attackDTO = AttackDTOMock.createMockAttackDTO("Mixed Attack", Arrays.asList("Fire", "Water", "Lightning"),
        3, "80", null);
    Type fireType = TypeMock.createMockTypeSavedInDb(1, "Fire");
    Type waterType = TypeMock.createMockTypeSavedInDb(2, "Water");
    Type lightningType = TypeMock.createMockTypeSavedInDb(3, "Lightning");

    when(typeMapper.toListEntity(attackDTO.getCost())).thenReturn(Arrays.asList(fireType, waterType, lightningType));
    when(typeService.getByLabel(fireType)).thenReturn(fireType);
    when(typeService.getByLabel(waterType)).thenReturn(waterType);
    when(typeService.getByLabel(lightningType)).thenReturn(lightningType);

    Map<Type, Integer> result = processor.findAndAggregateTypeCosts(attackDTO);

    assertThat(result).hasSize(3);
    assertThat(result).containsEntry(fireType, 1);
    assertThat(result).containsEntry(waterType, 1);
    assertThat(result).containsEntry(lightningType, 1);
    verify(typeMapper).toListEntity(attackDTO.getCost());
    verify(typeService).getByLabel(fireType);
    verify(typeService).getByLabel(waterType);
    verify(typeService).getByLabel(lightningType);
  }

  @Test
  void findAndAggregateTypeCosts_shouldHandleEmptyCost() {
    AttackDTO attackDTO = AttackDTOMock.createMockAttackDTO("No Cost", Collections.emptyList(), 0, "0", null);

    when(typeMapper.toListEntity(attackDTO.getCost())).thenReturn(Collections.emptyList());

    Map<Type, Integer> result = processor.findAndAggregateTypeCosts(attackDTO);

    assertThat(result).isEmpty();
    verify(typeMapper).toListEntity(attackDTO.getCost());
    verify(typeService, never()).getByLabel(any(Type.class));
  }

  @Test
  void createCostAttacksFromTypeMap_shouldCreateCostAttacksForAllTypes() {
    Attack attack = AttackMock.createMockAttackSavedInDb(3, "Mixed Cost", "50", 3, null);
    Type fireType = TypeMock.createMockTypeSavedInDb(1, "Fire");
    Type waterType = TypeMock.createMockTypeSavedInDb(2, "Water");
    Type psychicType = TypeMock.createMockTypeSavedInDb(4, "Psychic");

    Map<Type, Integer> typeCostsMap = new HashMap<>();
    typeCostsMap.put(fireType, 1);
    typeCostsMap.put(waterType, 1);
    typeCostsMap.put(psychicType, 1);

    CostAttack fireCostAttack = CostAttackMock.createMockCostAttackSavedInDb(4, attack, fireType, 1, false);
    CostAttack waterCostAttack = CostAttackMock.createMockCostAttackSavedInDb(5, attack, waterType, 1, false);
    CostAttack psychicCostAttack = CostAttackMock.createMockCostAttackSavedInDb(6, attack, psychicType, 1, false);

    when(costAttackMapper.toEntity(attack, fireType, 1)).thenReturn(fireCostAttack);
    when(costAttackMapper.toEntity(attack, waterType, 1)).thenReturn(waterCostAttack);
    when(costAttackMapper.toEntity(attack, psychicType, 1)).thenReturn(psychicCostAttack);

    List<CostAttack> result = processor.createCostAttacksFromTypeMap(attack, typeCostsMap);

    assertThat(result).hasSize(3);
    assertThat(result).containsExactlyInAnyOrder(fireCostAttack, waterCostAttack, psychicCostAttack);
    verify(costAttackMapper).toEntity(attack, fireType, 1);
    verify(costAttackMapper).toEntity(attack, waterType, 1);
    verify(costAttackMapper).toEntity(attack, psychicType, 1);
  }

  @Test
  void createCostAttacksFromTypeMap_shouldHandleEmptyMap() {
    Attack attack = AttackMock.createMockAttackSavedInDb(3, "Empty Cost", "10", 0, null);
    Map<Type, Integer> emptyMap = new HashMap<>();

    List<CostAttack> result = processor.createCostAttacksFromTypeMap(attack, emptyMap);

    assertThat(result).isEmpty();
    verify(costAttackMapper, never()).toEntity(any(Attack.class), any(Type.class), any(Integer.class));
  }

  @Test
  void createCostAttacksFromTypeMap_shouldHandleMultipleCostsOfSameType() {
    Attack attack = AttackMock.createMockAttackSavedInDb(7, "Double Fire", "70", 2, null);
    Type fireType = TypeMock.createMockTypeSavedInDb(1, "Fire");

    Map<Type, Integer> typeCostsMap = new HashMap<>();
    typeCostsMap.put(fireType, 2);

    CostAttack fireCostAttack = CostAttackMock.createMockCostAttackSavedInDb(7, attack, fireType, 2, false);

    when(costAttackMapper.toEntity(attack, fireType, 2)).thenReturn(fireCostAttack);

    List<CostAttack> result = processor.createCostAttacksFromTypeMap(attack, typeCostsMap);

    assertThat(result).hasSize(1);
    assertThat(result.get(0)).isEqualTo(fireCostAttack);
    assertThat(result.get(0).getCost()).isEqualTo(2);
    verify(costAttackMapper).toEntity(attack, fireType, 2);
  }
}