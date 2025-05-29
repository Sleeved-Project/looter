package com.sleeved.looter.infra.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sleeved.looter.domain.entity.atlas.Attack;
import com.sleeved.looter.domain.entity.atlas.CostAttack;
import com.sleeved.looter.domain.entity.atlas.Type;
import com.sleeved.looter.mock.domain.AttackMock;
import com.sleeved.looter.mock.domain.TypeMock;

@ExtendWith(MockitoExtension.class)
class CostAttackMapperTest {

  @InjectMocks
  private CostAttackMapper mapper;

  @Test
  void toEntity_shouldCreateCostAttackWithCorrectValues() {
    Attack attack = AttackMock.createMockAttackSavedInDb(1, "Thunderbolt", "90", 3, "Discard all Energy cards");
    Type type = TypeMock.createMockTypeSavedInDb(2, "Lightning");
    Integer cost = 2;

    CostAttack result = mapper.toEntity(attack, type, cost);

    assertThat(result).isNotNull();
    assertThat(result.getAttack()).isEqualTo(attack);
    assertThat(result.getType()).isEqualTo(type);
    assertThat(result.getCost()).isEqualTo(cost);
    assertThat(result.getIsFree()).isFalse();
  }

  @Test
  void toEntity_shouldHandleZeroCost() {
    Attack attack = AttackMock.createMockAttackSavedInDb(1, "Tackle", "10", 1, null);
    Type type = TypeMock.createMockTypeSavedInDb(3, "Colorless");
    Integer cost = 0;

    CostAttack result = mapper.toEntity(attack, type, cost);

    assertThat(result).isNotNull();
    assertThat(result.getAttack()).isEqualTo(attack);
    assertThat(result.getType()).isEqualTo(type);
    assertThat(result.getCost()).isEqualTo(0);
    assertThat(result.getIsFree()).isFalse();
  }

  @Test
  void toEntity_shouldHandleNullType() {
    Attack attack = AttackMock.createMockAttackSavedInDb(2, "Psyshock", "70", 2, "Flip a coin");
    Integer cost = 3;

    CostAttack result = mapper.toEntity(attack, null, cost);

    assertThat(result).isNotNull();
    assertThat(result.getAttack()).isEqualTo(attack);
    assertThat(result.getType()).isNull();
    assertThat(result.getCost()).isEqualTo(cost);
    assertThat(result.getIsFree()).isFalse();
  }

  @Test
  void toFreeAttackEntity_shouldCreateFreeAttack() {
    Attack attack = AttackMock.createMockAttackSavedInDb(3, "Rest", "0", 0, "Heal all damage");

    CostAttack result = mapper.toFreeAttackEntity(attack);

    assertThat(result).isNotNull();
    assertThat(result.getAttack()).isEqualTo(attack);
    assertThat(result.getType()).isNull();
    assertThat(result.getCost()).isEqualTo(0);
    assertThat(result.getIsFree()).isTrue();
  }

  @Test
  void toFreeAttackEntity_shouldHandleAttackWithNonZeroValues() {
    Attack attack = AttackMock.createMockAttackSavedInDb(4, "Special Move", "50", 2, "Special effect");

    CostAttack result = mapper.toFreeAttackEntity(attack);

    assertThat(result).isNotNull();
    assertThat(result.getAttack()).isEqualTo(attack);
    assertThat(result.getType()).isNull();
    assertThat(result.getCost()).isEqualTo(0);
    assertThat(result.getIsFree()).isTrue();
  }
}