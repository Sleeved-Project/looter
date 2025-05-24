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

import com.sleeved.looter.domain.entity.atlas.Ability;
import com.sleeved.looter.domain.repository.atlas.AbilityRepository;
import com.sleeved.looter.mock.domain.AbilityMock;

@ExtendWith(MockitoExtension.class)
class AbilityServiceTest {

  @Mock
  private AbilityRepository abilityRepository;

  @InjectMocks
  private AbilityService abilityService;

  @Test
  void getOrCreate_shouldReturnExistingAbility_whenAbilityExists() {
    Ability inputAbility = AbilityMock.createMockAbility("Flying",
        "This creature can only be blocked by creatures with flying.", "keyword");
    Ability existingAbility = AbilityMock.createMockAbilitySavedInDb(1, "Flying",
        "This creature can only be blocked by creatures with flying.", "keyword");

    when(abilityRepository.findByNameAndType("Flying", "keyword")).thenReturn(Optional.of(existingAbility));

    Ability result = abilityService.getOrCreate(inputAbility);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1);
    assertThat(result.getName()).isEqualTo("Flying");
    assertThat(result.getText()).isEqualTo("This creature can only be blocked by creatures with flying.");
    assertThat(result.getType()).isEqualTo("keyword");
    verify(abilityRepository).findByNameAndType("Flying", "keyword");
    verify(abilityRepository, never()).save(any(Ability.class));
  }

  @Test
  void getOrCreate_shouldCreateAndReturnNewAbility_whenAbilityDoesNotExist() {
    Ability inputAbility = AbilityMock.createMockAbility("First Strike",
        "This creature deals combat damage before creatures without first strike.", "keyword");
    Ability savedAbility = AbilityMock.createMockAbilitySavedInDb(2, "First Strike",
        "This creature deals combat damage before creatures without first strike.", "keyword");

    when(abilityRepository.findByNameAndType("First Strike", "keyword")).thenReturn(Optional.empty());
    when(abilityRepository.save(inputAbility)).thenReturn(savedAbility);

    Ability result = abilityService.getOrCreate(inputAbility);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(2);
    assertThat(result.getName()).isEqualTo("First Strike");
    assertThat(result.getText()).isEqualTo("This creature deals combat damage before creatures without first strike.");
    assertThat(result.getType()).isEqualTo("keyword");
    verify(abilityRepository).findByNameAndType("First Strike", "keyword");
    verify(abilityRepository).save(inputAbility);
  }

  @Test
  void getOrCreate_shouldHandleNullNameAndType() {
    Ability inputAbility = AbilityMock.createMockAbility(null, "Some text", null);
    Ability savedAbility = AbilityMock.createMockAbilitySavedInDb(3, null, "Some text", null);

    when(abilityRepository.findByNameAndType(null, null)).thenReturn(Optional.empty());
    when(abilityRepository.save(inputAbility)).thenReturn(savedAbility);

    Ability result = abilityService.getOrCreate(inputAbility);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(3);
    assertThat(result.getName()).isNull();
    assertThat(result.getText()).isEqualTo("Some text");
    assertThat(result.getType()).isNull();
    verify(abilityRepository).findByNameAndType(null, null);
    verify(abilityRepository).save(inputAbility);
  }
}
