package com.sleeved.looter.infra.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sleeved.looter.domain.entity.atlas.Attack;
import com.sleeved.looter.infra.dto.AttackDTO;
import com.sleeved.looter.mock.infra.AttackDTOMock;

@ExtendWith(MockitoExtension.class)
public class AttackMapperTest {

  @InjectMocks
  private AttackMapper mapper;

  @Test
  void toEntity_shouldMapValidDTO() {
    AttackDTO dto = AttackDTOMock.createMockAttackDTO("Fire Blast", List.of("Fire", "Fire"), 3, "120+",
        "Discard 2 Energy cards from this Pokémon.");

    Attack result = mapper.toEntity(dto);

    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo("Fire Blast");
    assertThat(result.getDamage()).isEqualTo("120+");
    assertThat(result.getConvertedEnergyCost()).isEqualTo(3);
    assertThat(result.getText()).isEqualTo("Discard 2 Energy cards from this Pokémon.");
  }

  @Test
  void toEntity_shouldHandleNullDTO() {
    Attack result = mapper.toEntity(null);

    assertThat(result).isNull();
  }

  @Test
  void toEntity_shouldHandleDTOWithNullValues() {
    AttackDTO dto = AttackDTOMock.createMockAttackDTO(null, null, 0, null, null);

    Attack result = mapper.toEntity(dto);

    assertThat(result).isNotNull();
    assertThat(result.getName()).isNull();
    assertThat(result.getDamage()).isNull();
    assertThat(result.getConvertedEnergyCost()).isEqualTo(0);
    assertThat(result.getText()).isNull();
  }

  @Test
  void toEntity_shouldMapDTOWithPartialValues() {
    AttackDTO dto = AttackDTOMock.createMockAttackDTO("Quick Attack", List.of("Colorless"), 1, "20", null);

    Attack result = mapper.toEntity(dto);

    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo("Quick Attack");
    assertThat(result.getDamage()).isEqualTo("20");
    assertThat(result.getConvertedEnergyCost()).isEqualTo(1);
    assertThat(result.getText()).isNull();
  }

  @Test
  void toEntity_shouldHandleEmptyStringValues() {
    AttackDTO dto = AttackDTOMock.createMockAttackDTO("Quick Attack", List.of("Colorless"), 1, "", "");

    Attack result = mapper.toEntity(dto);

    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo("Quick Attack");
    assertThat(result.getDamage()).isNull();
    assertThat(result.getConvertedEnergyCost()).isEqualTo(1);
    assertThat(result.getText()).isNull();
  }

  @Test
  void toEntity_shouldSetConvertedEnergyCostToZero_whenCostIsEmpty() {
    AttackDTO dto = AttackDTOMock.createMockAttackDTO("Empty Cost Attack", List.of(), 5, "30", "Text");

    Attack result = mapper.toEntity(dto);

    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo("Empty Cost Attack");
    assertThat(result.getDamage()).isEqualTo("30");
    assertThat(result.getConvertedEnergyCost()).isEqualTo(0);
    assertThat(result.getText()).isEqualTo("Text");
  }

  @Test
  void toEntity_shouldSetConvertedEnergyCostToZero_whenCostIsNull() {
    AttackDTO dto = AttackDTOMock.createMockAttackDTO("Null Cost Attack", null, 3, "40", "Text");

    Attack result = mapper.toEntity(dto);

    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo("Null Cost Attack");
    assertThat(result.getDamage()).isEqualTo("40");
    assertThat(result.getConvertedEnergyCost()).isEqualTo(0);
    assertThat(result.getText()).isEqualTo("Text");
  }

  @Test
  void toEntity_shouldSetConvertedEnergyCostToZero_whenCostIsFree() {
    AttackDTO dto = AttackDTOMock.createMockAttackDTO("Free Attack", List.of("Free"), 2, "10", "This is free");

    Attack result = mapper.toEntity(dto);

    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo("Free Attack");
    assertThat(result.getDamage()).isEqualTo("10");
    assertThat(result.getConvertedEnergyCost()).isEqualTo(0);
    assertThat(result.getText()).isEqualTo("This is free");
  }

  @Test
  void toListEntity_shouldMapMultipleAttacks() {
    List<AttackDTO> dtos = AttackDTOMock.createMockAttackDTOsList(2);

    List<Attack> results = mapper.toListEntity(dtos);

    assertThat(results).hasSize(2);

    assertThat(results.get(0).getName()).isEqualTo("Name 1");
    assertThat(results.get(0).getDamage()).isEqualTo("Damage 1");
    assertThat(results.get(0).getConvertedEnergyCost()).isEqualTo(1);
    assertThat(results.get(0).getText()).isEqualTo("Text 1");

    assertThat(results.get(1).getName()).isEqualTo("Name 2");
    assertThat(results.get(1).getDamage()).isEqualTo("Damage 2");
    assertThat(results.get(1).getConvertedEnergyCost()).isEqualTo(2);
    assertThat(results.get(1).getText()).isEqualTo("Text 2");
  }

  @Test
  void toListEntity_shouldHandleNullList() {
    List<Attack> results = mapper.toListEntity(null);

    assertThat(results).isEmpty();
  }

  @Test
  void toListEntity_shouldHandleEmptyList() {
    List<AttackDTO> emptyList = AttackDTOMock.createMockAttackDTOsList(0);

    List<Attack> results = mapper.toListEntity(emptyList);

    assertThat(results).isEmpty();
  }

  @Test
  void toListEntity_shouldFilterNullElements() {
    List<AttackDTO> dtos = new ArrayList<>(AttackDTOMock.createMockAttackDTOsList(1));
    dtos.add(null);

    List<Attack> results = mapper.toListEntity(dtos);

    assertThat(results).hasSize(1);
    assertThat(results.get(0).getName()).isEqualTo("Name 1");
    assertThat(results.get(0).getDamage()).isEqualTo("Damage 1");
    assertThat(results.get(0).getConvertedEnergyCost()).isEqualTo(1);
    assertThat(results.get(0).getText()).isEqualTo("Text 1");
  }

  @Test
  void toListEntity_shouldCorrectlyProcessMixedCostTypes() {
    List<AttackDTO> dtos = new ArrayList<>();
    dtos.add(AttackDTOMock.createMockAttackDTO("Normal Attack", List.of("Fire"), 1, "30", "Normal"));
    dtos.add(AttackDTOMock.createMockAttackDTO("Free Attack", List.of("Free"), 3, "10", "Free"));
    dtos.add(AttackDTOMock.createMockAttackDTO("Empty Attack", List.of(), 2, "20", "Empty"));

    List<Attack> results = mapper.toListEntity(dtos);

    assertThat(results).hasSize(3);

    assertThat(results.get(0).getName()).isEqualTo("Normal Attack");
    assertThat(results.get(0).getConvertedEnergyCost()).isEqualTo(1);

    assertThat(results.get(1).getName()).isEqualTo("Free Attack");
    assertThat(results.get(1).getConvertedEnergyCost()).isEqualTo(0);

    assertThat(results.get(2).getName()).isEqualTo("Empty Attack");
    assertThat(results.get(2).getConvertedEnergyCost()).isEqualTo(0);
  }

  @Test
  void isFreeOrEmptyCost_shouldReturnTrue_whenCostIsNull() {
    boolean result = mapper.isFreeOrEmptyCost(null);

    assertThat(result).isTrue();
  }

  @Test
  void isFreeOrEmptyCost_shouldReturnTrue_whenCostIsEmpty() {
    List<String> emptyCost = Collections.emptyList();

    boolean result = mapper.isFreeOrEmptyCost(emptyCost);

    assertThat(result).isTrue();
  }

  @Test
  void isFreeOrEmptyCost_shouldReturnTrue_whenCostIsFree() {
    List<String> freeCost = Collections.singletonList("Free");

    boolean result = mapper.isFreeOrEmptyCost(freeCost);

    assertThat(result).isTrue();
  }

  @Test
  void isFreeOrEmptyCost_shouldReturnFalse_whenCostHasOneNonFreeElement() {
    List<String> singleNonFreeCost = Collections.singletonList("Fire");

    boolean result = mapper.isFreeOrEmptyCost(singleNonFreeCost);

    assertThat(result).isFalse();
  }

  @Test
  void isFreeOrEmptyCost_shouldReturnFalse_whenCostHasMultipleElements() {
    List<String> multipleElementsCost = Arrays.asList("Fire", "Water");

    boolean result = mapper.isFreeOrEmptyCost(multipleElementsCost);

    assertThat(result).isFalse();
  }

  @Test
  void isFreeOrEmptyCost_shouldReturnFalse_whenCostHasFreeWithOtherElements() {
    List<String> mixedCost = Arrays.asList("Free", "Water");

    boolean result = mapper.isFreeOrEmptyCost(mixedCost);

    assertThat(result).isFalse();
  }

  @Test
  void isFreeOrEmptyCost_shouldBeCaseSensitive() {
    List<String> lowerCaseCost = Collections.singletonList("free");

    boolean result = mapper.isFreeOrEmptyCost(lowerCaseCost);

    assertThat(result).isFalse();
  }
}
