package com.sleeved.looter.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sleeved.looter.domain.entity.atlas.Attack;
import com.sleeved.looter.domain.entity.atlas.CostAttack;
import com.sleeved.looter.domain.entity.atlas.Type;
import com.sleeved.looter.domain.repository.atlas.CostAttackRepository;
import com.sleeved.looter.mock.domain.AttackMock;
import com.sleeved.looter.mock.domain.CostAttackMock;
import com.sleeved.looter.mock.domain.TypeMock;

@ExtendWith(MockitoExtension.class)
class CostAttackServiceTest {

  @Mock
  private CostAttackRepository costAttackRepository;

  @InjectMocks
  private CostAttackService costAttackService;

  @Test
  void getOrCreate_shouldReturnExistingCostAttack_whenFreeAttackExists() {
    Attack attack = AttackMock.createMockAttackSavedInDb(1, "Retreat", "0", 0, "This is a free action");

    CostAttack inputCostAttack = CostAttackMock.createMockFreeCostAttack(attack);
    CostAttack existingCostAttack = CostAttackMock.createMockFreeCostAttackSavedInDb(101, attack);

    when(costAttackRepository.findByAttackAndIsFree(attack, true))
        .thenReturn(Optional.of(existingCostAttack));

    CostAttack result = costAttackService.getOrCreate(inputCostAttack);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(101);
    assertThat(result.getAttack()).isEqualTo(attack);
    assertThat(result.getType()).isNull();
    assertThat(result.getCost()).isEqualTo(0);
    assertThat(result.getIsFree()).isTrue();

    verify(costAttackRepository).findByAttackAndIsFree(attack, true);
    verify(costAttackRepository, never()).save(any(CostAttack.class));
  }

  @Test
  void getOrCreate_shouldCreateAndReturnNewCostAttack_whenFreeAttackDoesNotExist() {
    Attack attack = AttackMock.createMockAttackSavedInDb(2, "Rest", "0", 0, "Recover from all status conditions");

    CostAttack inputCostAttack = CostAttackMock.createMockFreeCostAttack(attack);
    CostAttack savedCostAttack = CostAttackMock.createMockFreeCostAttackSavedInDb(102, attack);

    when(costAttackRepository.findByAttackAndIsFree(attack, true))
        .thenReturn(Optional.empty());
    when(costAttackRepository.save(inputCostAttack))
        .thenReturn(savedCostAttack);

    CostAttack result = costAttackService.getOrCreate(inputCostAttack);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(102);
    assertThat(result.getAttack()).isEqualTo(attack);
    assertThat(result.getType()).isNull();
    assertThat(result.getCost()).isEqualTo(0);
    assertThat(result.getIsFree()).isTrue();

    verify(costAttackRepository).findByAttackAndIsFree(attack, true);
    verify(costAttackRepository).save(inputCostAttack);
  }

  @Test
  void getOrCreate_shouldReturnExistingCostAttack_whenNonFreeAttackExists() {
    Attack attack = AttackMock.createMockAttackSavedInDb(3, "Fire Blast", "120", 3, "Discard 2 Energy cards");
    Type type = TypeMock.createMockTypeSavedInDb(1, "Fire");
    Integer cost = 2;
    Boolean isFree = false;

    CostAttack inputCostAttack = CostAttackMock.createMockCostAttack(attack, type, cost, isFree);
    CostAttack existingCostAttack = CostAttackMock.createMockCostAttackSavedInDb(103, attack, type, cost, isFree);

    when(costAttackRepository.findByAttackAndTypeAndCost(attack, type, cost))
        .thenReturn(Optional.of(existingCostAttack));

    CostAttack result = costAttackService.getOrCreate(inputCostAttack);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(103);
    assertThat(result.getAttack()).isEqualTo(attack);
    assertThat(result.getType()).isEqualTo(type);
    assertThat(result.getCost()).isEqualTo(cost);
    assertThat(result.getIsFree()).isFalse();

    verify(costAttackRepository).findByAttackAndTypeAndCost(attack, type, cost);
    verify(costAttackRepository, never()).save(any(CostAttack.class));
  }

  @Test
  void getOrCreate_shouldCreateAndReturnNewCostAttack_whenNonFreeAttackDoesNotExist() {
    Attack attack = AttackMock.createMockAttackSavedInDb(4, "Hydro Pump", "60+", 2, "Damage bonus for water energy");
    Type type = TypeMock.createMockTypeSavedInDb(2, "Water");
    Integer cost = 1;
    Boolean isFree = false;

    CostAttack inputCostAttack = CostAttackMock.createMockCostAttack(attack, type, cost, isFree);
    CostAttack savedCostAttack = CostAttackMock.createMockCostAttackSavedInDb(104, attack, type, cost, isFree);

    when(costAttackRepository.findByAttackAndTypeAndCost(attack, type, cost))
        .thenReturn(Optional.empty());
    when(costAttackRepository.save(inputCostAttack))
        .thenReturn(savedCostAttack);

    CostAttack result = costAttackService.getOrCreate(inputCostAttack);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(104);
    assertThat(result.getAttack()).isEqualTo(attack);
    assertThat(result.getType()).isEqualTo(type);
    assertThat(result.getCost()).isEqualTo(cost);
    assertThat(result.getIsFree()).isFalse();

    verify(costAttackRepository).findByAttackAndTypeAndCost(attack, type, cost);
    verify(costAttackRepository).save(inputCostAttack);
  }

  @Test
  void getOrCreate_shouldHandleNullType_forNonFreeAttack() {
    Attack attack = AttackMock.createMockAttackSavedInDb(5, "Colorless Attack", "40", 1, "Basic attack");
    Integer cost = 1;
    Boolean isFree = false;

    CostAttack inputCostAttack = CostAttackMock.createMockCostAttack(attack, null, cost, isFree);
    CostAttack savedCostAttack = CostAttackMock.createMockCostAttackSavedInDb(105, attack, null, cost, isFree);

    when(costAttackRepository.findByAttackAndTypeAndCost(attack, null, cost))
        .thenReturn(Optional.empty());
    when(costAttackRepository.save(inputCostAttack))
        .thenReturn(savedCostAttack);

    CostAttack result = costAttackService.getOrCreate(inputCostAttack);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(105);
    assertThat(result.getAttack()).isEqualTo(attack);
    assertThat(result.getType()).isNull();
    assertThat(result.getCost()).isEqualTo(cost);
    assertThat(result.getIsFree()).isFalse();

    verify(costAttackRepository).findByAttackAndTypeAndCost(attack, null, cost);
    verify(costAttackRepository).save(inputCostAttack);
  }
}