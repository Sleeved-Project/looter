package com.sleeved.looter.infra.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sleeved.looter.domain.entity.atlas.Ability;
import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.entity.atlas.CardAbility;
import com.sleeved.looter.mock.domain.AbilityMock;
import com.sleeved.looter.mock.domain.CardMock;

@ExtendWith(MockitoExtension.class)
public class CardAbilityMapperTest {

  @InjectMocks
  private CardAbilityMapper mapper;

  @Test
  void toEntity_shouldMapValidEntities() {
    Ability ability = AbilityMock.createMockAbility("Quick Attack", "This attack does 20 damage.", "Pokémon Power");
    Card card = CardMock.createBasicMockCard("card-123", "Pikachu");

    CardAbility result = mapper.toEntity(ability, card);

    assertThat(result).isNotNull();
    assertThat(result.getAbility()).isEqualTo(ability);
    assertThat(result.getCard()).isEqualTo(card);
  }

  @Test
  void toEntity_shouldMapEntitiesWithIds() {
    Ability ability = AbilityMock.createMockAbilitySavedInDb(42, "Thundershock", "This attack does 30 damage.",
        "Attack");
    Card card = CardMock.createBasicMockCard("card-456", "Raichu");

    CardAbility result = mapper.toEntity(ability, card);

    assertThat(result).isNotNull();
    assertThat(result.getAbility()).isEqualTo(ability);
    assertThat(result.getAbility().getId()).isEqualTo(42);
    assertThat(result.getCard()).isEqualTo(card);
    assertThat(result.getCard().getId()).isEqualTo("card-456");
  }

  @Test
  void toEntity_shouldReturnNullWithNullAbility() {
    Card card = CardMock.createBasicMockCard("card-789", "Charizard");

    CardAbility result = mapper.toEntity(null, card);

    assertThat(result).isNull();
  }

  @Test
  void toEntity_shouldReturnNullWithNullCard() {
    Ability ability = AbilityMock.createMockAbility("Flamethrower", "This attack does 50 damage.", "Attack");

    CardAbility result = mapper.toEntity(ability, null);

    assertThat(result).isNull();
  }

  @Test
  void toEntity_shouldReturnNullWithBothInputsNull() {
    CardAbility result = mapper.toEntity(null, null);

    assertThat(result).isNull();
  }
}