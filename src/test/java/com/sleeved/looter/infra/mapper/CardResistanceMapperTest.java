package com.sleeved.looter.infra.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.entity.atlas.CardResistance;
import com.sleeved.looter.domain.entity.atlas.Resistance;
import com.sleeved.looter.domain.entity.atlas.Type;
import com.sleeved.looter.mock.domain.CardMock;
import com.sleeved.looter.mock.domain.ResistanceMock;
import com.sleeved.looter.mock.domain.TypeMock;

@ExtendWith(MockitoExtension.class)
public class CardResistanceMapperTest {

  @InjectMocks
  private CardResistanceMapper mapper;

  @Test
  void toEntity_shouldMapValidEntities() {
    Type type = TypeMock.createMockType("Fighting");
    Resistance resistance = ResistanceMock.createMockResistance(type, "-30");
    Card card = CardMock.createBasicMockCard("card-123", "Pikachu");

    CardResistance result = mapper.toEntity(resistance, card);

    assertThat(result).isNotNull();
    assertThat(result.getResistance()).isEqualTo(resistance);
    assertThat(result.getCard()).isEqualTo(card);
    assertThat(result.getResistance().getType().getLabel()).isEqualTo("Fighting");
    assertThat(result.getResistance().getValue()).isEqualTo("-30");
  }

  @Test
  void toEntity_shouldMapEntitiesWithIds() {
    Type type = TypeMock.createMockTypeSavedInDb(42, "Water");
    Resistance resistance = ResistanceMock.createMockResistanceSavedInDb(99, type, "-20");
    Card card = CardMock.createBasicMockCard("card-456", "Charizard");

    CardResistance result = mapper.toEntity(resistance, card);

    assertThat(result).isNotNull();
    assertThat(result.getResistance()).isEqualTo(resistance);
    assertThat(result.getResistance().getId()).isEqualTo(99);
    assertThat(result.getResistance().getType().getId()).isEqualTo(42);
    assertThat(result.getCard()).isEqualTo(card);
    assertThat(result.getCard().getId()).isEqualTo("card-456");
  }

  @Test
  void toEntity_shouldReturnNullWithNullResistance() {
    Card card = CardMock.createBasicMockCard("card-789", "Mewtwo");

    CardResistance result = mapper.toEntity(null, card);

    assertThat(result).isNull();
  }

  @Test
  void toEntity_shouldReturnNullWithNullCard() {
    Type type = TypeMock.createMockType("Lightning");
    Resistance resistance = ResistanceMock.createMockResistance(type, "-30");

    CardResistance result = mapper.toEntity(resistance, null);

    assertThat(result).isNull();
  }

  @Test
  void toEntity_shouldReturnNullWithBothInputsNull() {
    CardResistance result = mapper.toEntity(null, null);

    assertThat(result).isNull();
  }
}