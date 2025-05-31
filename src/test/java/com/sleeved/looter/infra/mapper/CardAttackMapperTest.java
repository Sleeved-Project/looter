package com.sleeved.looter.infra.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sleeved.looter.domain.entity.atlas.Attack;
import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.entity.atlas.CardAttack;
import com.sleeved.looter.mock.domain.AttackMock;
import com.sleeved.looter.mock.domain.CardMock;

@ExtendWith(MockitoExtension.class)
public class CardAttackMapperTest {

  @InjectMocks
  private CardAttackMapper mapper;

  @Test
  void toEntity_shouldMapValidEntities() {
    Attack attack = AttackMock.createMockAttack("Thunderbolt", "120", 3,
        "Discard all Energy attached to this Pokémon.");
    Card card = CardMock.createBasicMockCard("card-123", "Pikachu");

    CardAttack result = mapper.toEntity(attack, card);

    assertThat(result).isNotNull();
    assertThat(result.getAttack()).isEqualTo(attack);
    assertThat(result.getCard()).isEqualTo(card);
    assertThat(result.getAttack().getName()).isEqualTo("Thunderbolt");
    assertThat(result.getCard().getId()).isEqualTo("card-123");
  }

  @Test
  void toEntity_shouldMapEntitiesWithIds() {
    Attack attack = AttackMock.createMockAttackSavedInDb(42, "Hyper Beam", "200", 4,
        "Discard 2 Energy from this Pokémon.");
    Card card = CardMock.createBasicMockCard("card-456", "Mewtwo");

    CardAttack result = mapper.toEntity(attack, card);

    assertThat(result).isNotNull();
    assertThat(result.getAttack()).isEqualTo(attack);
    assertThat(result.getAttack().getId()).isEqualTo(42);
    assertThat(result.getCard()).isEqualTo(card);
  }

  @Test
  void toEntity_shouldReturnNullWithNullAttack() {
    Card card = CardMock.createBasicMockCard("card-789", "Charizard");

    CardAttack result = mapper.toEntity(null, card);

    assertThat(result).isNull();
  }

  @Test
  void toEntity_shouldReturnNullWithNullCard() {
    Attack attack = AttackMock.createMockAttack("Fire Spin", "150", 4,
        "Discard 2 Fire Energy attached to this Pokémon.");

    CardAttack result = mapper.toEntity(attack, null);

    assertThat(result).isNull();
  }

  @Test
  void toEntity_shouldReturnNullWithBothInputsNull() {
    CardAttack result = mapper.toEntity(null, null);

    assertThat(result).isNull();
  }
}