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
import com.sleeved.looter.domain.repository.atlas.AttackRepository;
import com.sleeved.looter.mock.domain.AttackMock;

@ExtendWith(MockitoExtension.class)
class AttackServiceTest {

  @Mock
  private AttackRepository attackRepository;

  @InjectMocks
  private AttackService attackService;

  @Test
  void getOrCreate_shouldReturnExistingAttack_whenAttackExists() {
    Attack inputAttack = AttackMock.createMockAttack("Thunderbolt", "90", 3,
        "Discard all Energy cards attached to this Pokémon.");
    Attack existingAttack = AttackMock.createMockAttackSavedInDb(1, "Thunderbolt", "90", 3,
        "Discard all Energy cards attached to this Pokémon.");

    when(attackRepository.findByNameAndDamageAndConvertedEnergyCost("Thunderbolt", "90", 3))
        .thenReturn(Optional.of(existingAttack));

    Attack result = attackService.getOrCreate(inputAttack);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1);
    assertThat(result.getName()).isEqualTo("Thunderbolt");
    assertThat(result.getDamage()).isEqualTo("90");
    assertThat(result.getConvertedEnergyCost()).isEqualTo(3);
    assertThat(result.getText()).isEqualTo("Discard all Energy cards attached to this Pokémon.");
    verify(attackRepository).findByNameAndDamageAndConvertedEnergyCost("Thunderbolt", "90", 3);
    verify(attackRepository, never()).save(any(Attack.class));
  }

  @Test
  void getOrCreate_shouldCreateAndReturnNewAttack_whenAttackDoesNotExist() {
    Attack inputAttack = AttackMock.createMockAttack("Hydro Pump", "50+", 2,
        "This attack does 10 more damage for each Water Energy attached to this Pokémon.");
    Attack savedAttack = AttackMock.createMockAttackSavedInDb(2, "Hydro Pump", "50+", 2,
        "This attack does 10 more damage for each Water Energy attached to this Pokémon.");

    when(attackRepository.findByNameAndDamageAndConvertedEnergyCost("Hydro Pump", "50+", 2))
        .thenReturn(Optional.empty());
    when(attackRepository.save(inputAttack)).thenReturn(savedAttack);

    Attack result = attackService.getOrCreate(inputAttack);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(2);
    assertThat(result.getName()).isEqualTo("Hydro Pump");
    assertThat(result.getDamage()).isEqualTo("50+");
    assertThat(result.getConvertedEnergyCost()).isEqualTo(2);
    assertThat(result.getText())
        .isEqualTo("This attack does 10 more damage for each Water Energy attached to this Pokémon.");
    verify(attackRepository).findByNameAndDamageAndConvertedEnergyCost("Hydro Pump", "50+", 2);
    verify(attackRepository).save(inputAttack);
  }

  @Test
  void getOrCreate_shouldHandleNullValues() {
    Attack inputAttack = AttackMock.createMockAttack(null, null, null, "Some text");
    Attack savedAttack = AttackMock.createMockAttackSavedInDb(3, null, null, null, "Some text");

    when(attackRepository.findByNameAndDamageAndConvertedEnergyCost(null, null, null))
        .thenReturn(Optional.empty());
    when(attackRepository.save(inputAttack)).thenReturn(savedAttack);

    Attack result = attackService.getOrCreate(inputAttack);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(3);
    assertThat(result.getName()).isNull();
    assertThat(result.getDamage()).isNull();
    assertThat(result.getConvertedEnergyCost()).isNull();
    assertThat(result.getText()).isEqualTo("Some text");
    verify(attackRepository).findByNameAndDamageAndConvertedEnergyCost(null, null, null);
    verify(attackRepository).save(inputAttack);
  }
}
