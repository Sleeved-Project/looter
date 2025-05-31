package com.sleeved.looter.infra.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.entity.atlas.CardType;
import com.sleeved.looter.domain.entity.atlas.Type;
import com.sleeved.looter.mock.domain.CardMock;
import com.sleeved.looter.mock.domain.TypeMock;

@ExtendWith(MockitoExtension.class)
public class CardTypeMapperTest {

  @InjectMocks
  private CardTypeMapper mapper;

  @Test
  void toEntity_shouldMapValidEntities() {
    Type type = TypeMock.createMockType("Grass");
    Card card = CardMock.createBasicMockCard("card-123", "Bulbasaur");

    CardType result = mapper.toEntity(type, card);

    assertThat(result).isNotNull();
    assertThat(result.getType()).isEqualTo(type);
    assertThat(result.getCard()).isEqualTo(card);
    assertThat(result.getType().getLabel()).isEqualTo("Grass");
    assertThat(result.getCard().getId()).isEqualTo("card-123");
    assertThat(result.getCard().getName()).isEqualTo("Bulbasaur");
  }

  @Test
  void toEntity_shouldMapEntitiesWithIds() {
    Type type = TypeMock.createMockTypeSavedInDb(42, "Fire");
    Card card = CardMock.createBasicMockCard("card-456", "Charmander");

    CardType result = mapper.toEntity(type, card);

    assertThat(result).isNotNull();
    assertThat(result.getType()).isEqualTo(type);
    assertThat(result.getType().getId()).isEqualTo(42);
    assertThat(result.getType().getLabel()).isEqualTo("Fire");
    assertThat(result.getCard()).isEqualTo(card);
    assertThat(result.getCard().getId()).isEqualTo("card-456");
  }

  @Test
  void toEntity_shouldReturnNullWithNullType() {
    Card card = CardMock.createBasicMockCard("card-789", "Squirtle");

    CardType result = mapper.toEntity(null, card);

    assertThat(result).isNull();
  }

  @Test
  void toEntity_shouldReturnNullWithNullCard() {
    Type type = TypeMock.createMockType("Water");

    CardType result = mapper.toEntity(type, null);

    assertThat(result).isNull();
  }

  @Test
  void toEntity_shouldReturnNullWithBothInputsNull() {
    CardType result = mapper.toEntity(null, null);

    assertThat(result).isNull();
  }
}