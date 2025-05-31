package com.sleeved.looter.infra.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.entity.atlas.CardWeakness;
import com.sleeved.looter.domain.entity.atlas.Type;
import com.sleeved.looter.domain.entity.atlas.Weakness;
import com.sleeved.looter.mock.domain.CardMock;
import com.sleeved.looter.mock.domain.TypeMock;
import com.sleeved.looter.mock.domain.WeaknessMock;

@ExtendWith(MockitoExtension.class)
public class CardWeaknessMapperTest {

  @InjectMocks
  private CardWeaknessMapper mapper;

  @Test
  void toEntity_shouldMapValidEntities() {
    Type type = TypeMock.createMockType("Fire");
    Weakness weakness = WeaknessMock.createMockWeakness(type, "×2");
    Card card = CardMock.createBasicMockCard("card-123", "Bulbasaur");

    CardWeakness result = mapper.toEntity(weakness, card);

    assertThat(result).isNotNull();
    assertThat(result.getWeakness()).isEqualTo(weakness);
    assertThat(result.getCard()).isEqualTo(card);
    assertThat(result.getWeakness().getType().getLabel()).isEqualTo("Fire");
    assertThat(result.getWeakness().getValue()).isEqualTo("×2");
    assertThat(result.getCard().getId()).isEqualTo("card-123");
    assertThat(result.getCard().getName()).isEqualTo("Bulbasaur");
  }

  @Test
  void toEntity_shouldMapEntitiesWithIds() {
    Type type = TypeMock.createMockTypeSavedInDb(42, "Electric");
    Weakness weakness = WeaknessMock.createMockWeaknessSavedInDb(99, type, "×2");
    Card card = CardMock.createBasicMockCard("card-456", "Squirtle");

    CardWeakness result = mapper.toEntity(weakness, card);

    assertThat(result).isNotNull();
    assertThat(result.getWeakness()).isEqualTo(weakness);
    assertThat(result.getWeakness().getId()).isEqualTo(99);
    assertThat(result.getWeakness().getType().getId()).isEqualTo(42);
    assertThat(result.getCard()).isEqualTo(card);
  }

  @Test
  void toEntity_shouldReturnNullWithNullWeakness() {
    Card card = CardMock.createBasicMockCard("card-789", "Charizard");

    CardWeakness result = mapper.toEntity(null, card);

    assertThat(result).isNull();
  }

  @Test
  void toEntity_shouldReturnNullWithNullCard() {
    Type type = TypeMock.createMockType("Water");
    Weakness weakness = WeaknessMock.createMockWeakness(type, "×2");

    CardWeakness result = mapper.toEntity(weakness, null);

    assertThat(result).isNull();
  }

  @Test
  void toEntity_shouldReturnNullWithBothInputsNull() {
    CardWeakness result = mapper.toEntity(null, null);

    assertThat(result).isNull();
  }
}