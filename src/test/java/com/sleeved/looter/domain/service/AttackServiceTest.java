package com.sleeved.looter.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

        when(attackRepository.findByNameAndDamageAndConvertedEnergyCostAndText(
                "Thunderbolt", "90", 3, "Discard all Energy cards attached to this Pokémon."))
                .thenReturn(Optional.of(existingAttack));

        Attack result = attackService.getOrCreate(inputAttack);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("Thunderbolt");
        assertThat(result.getDamage()).isEqualTo("90");
        assertThat(result.getConvertedEnergyCost()).isEqualTo(3);
        assertThat(result.getText()).isEqualTo("Discard all Energy cards attached to this Pokémon.");

        verify(attackRepository).findByNameAndDamageAndConvertedEnergyCostAndText(
                "Thunderbolt", "90", 3, "Discard all Energy cards attached to this Pokémon.");
        verify(attackRepository, never()).save(any(Attack.class));
    }

    @Test
    void getOrCreate_shouldCreateAndReturnNewAttack_whenAttackDoesNotExist() {
        Attack inputAttack = AttackMock.createMockAttack("Hydro Pump", "50+", 2,
                "This attack does 10 more damage for each Water Energy attached to this Pokémon.");
        Attack savedAttack = AttackMock.createMockAttackSavedInDb(2, "Hydro Pump", "50+", 2,
                "This attack does 10 more damage for each Water Energy attached to this Pokémon.");

        when(attackRepository.findByNameAndDamageAndConvertedEnergyCostAndText(
                "Hydro Pump", "50+", 2,
                "This attack does 10 more damage for each Water Energy attached to this Pokémon."))
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

        verify(attackRepository).findByNameAndDamageAndConvertedEnergyCostAndText(
                "Hydro Pump", "50+", 2,
                "This attack does 10 more damage for each Water Energy attached to this Pokémon.");
        verify(attackRepository).save(inputAttack);
    }

    @Test
    void getOrCreate_shouldHandleNullValues() {
        Attack inputAttack = AttackMock.createMockAttack("Attack Name", null, 0, null);
        Attack savedAttack = AttackMock.createMockAttackSavedInDb(3, "Attack Name", null, 0, null);

        when(attackRepository.findByNameAndDamageAndConvertedEnergyCostAndText("Attack Name", null, 0, null))
                .thenReturn(Optional.empty());
        when(attackRepository.save(inputAttack)).thenReturn(savedAttack);

        Attack result = attackService.getOrCreate(inputAttack);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(3);
        assertThat(result.getName()).isEqualTo("Attack Name");
        assertThat(result.getDamage()).isNull();
        assertThat(result.getConvertedEnergyCost()).isEqualTo(0);
        assertThat(result.getText()).isNull();

        verify(attackRepository).findByNameAndDamageAndConvertedEnergyCostAndText("Attack Name", null, 0, null);
        verify(attackRepository).save(inputAttack);
    }

    @Test
    void getByNameAndDamageAndConvertedEnegyCostAndText_shouldReturnAttack_whenAttackExists() {
        Attack inputAttack = AttackMock.createMockAttack("Tackle", "20", 1, null);
        Attack existingAttack = AttackMock.createMockAttackSavedInDb(4, "Tackle", "20", 1, null);

        when(attackRepository.findByNameAndDamageAndConvertedEnergyCostAndText("Tackle", "20", 1, null))
                .thenReturn(Optional.of(existingAttack));

        Attack result = attackService.getByNameAndDamageAndConvertedEnegyCostAndText(inputAttack);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(4);
        assertThat(result.getName()).isEqualTo("Tackle");
        assertThat(result.getDamage()).isEqualTo("20");
        assertThat(result.getConvertedEnergyCost()).isEqualTo(1);
        assertThat(result.getText()).isNull();

        verify(attackRepository).findByNameAndDamageAndConvertedEnergyCostAndText("Tackle", "20", 1, null);
    }

    @Test
    void getByNameAndDamageAndConvertedEnegyCostAndText_shouldThrowException_whenAttackDoesNotExist() {
        Attack inputAttack = AttackMock.createMockAttack("Nonexistent", "100", 5, "Some text");

        when(attackRepository.findByNameAndDamageAndConvertedEnergyCostAndText("Nonexistent", "100", 5, "Some text"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> attackService.getByNameAndDamageAndConvertedEnegyCostAndText(inputAttack))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Attack not found for name: Nonexistent");

        verify(attackRepository).findByNameAndDamageAndConvertedEnergyCostAndText("Nonexistent", "100", 5, "Some text");
    }

    @Test
    void getByNameAndDamageAndConvertedEnegyCostAndText_shouldHandleFreeAttack() {
        Attack inputAttack = AttackMock.createMockAttack("Free Attack", "10", 0, "This is a free attack");
        Attack existingAttack = AttackMock.createMockAttackSavedInDb(5, "Free Attack", "10", 0,
                "This is a free attack");

        when(attackRepository.findByNameAndDamageAndConvertedEnergyCostAndText("Free Attack", "10", 0,
                "This is a free attack"))
                .thenReturn(Optional.of(existingAttack));

        Attack result = attackService.getByNameAndDamageAndConvertedEnegyCostAndText(inputAttack);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(5);
        assertThat(result.getName()).isEqualTo("Free Attack");
        assertThat(result.getDamage()).isEqualTo("10");
        assertThat(result.getConvertedEnergyCost()).isEqualTo(0);
        assertThat(result.getText()).isEqualTo("This is a free attack");

        verify(attackRepository).findByNameAndDamageAndConvertedEnergyCostAndText("Free Attack", "10", 0,
                "This is a free attack");
    }
}
