package com.sleeved.looter.infra.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
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
    assertThat(result.getDamage()).isNull(); // Vérifie que les chaînes vides sont converties en null
    assertThat(result.getConvertedEnergyCost()).isEqualTo(1);
    assertThat(result.getText()).isNull(); // Vérifie que les chaînes vides sont converties en null
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
}
