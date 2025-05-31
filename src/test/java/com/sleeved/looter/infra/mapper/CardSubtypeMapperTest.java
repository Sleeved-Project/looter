package com.sleeved.looter.infra.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.entity.atlas.CardSubtype;
import com.sleeved.looter.domain.entity.atlas.Subtype;
import com.sleeved.looter.mock.domain.CardMock;
import com.sleeved.looter.mock.domain.SubtypeMock;

@ExtendWith(MockitoExtension.class)
public class CardSubtypeMapperTest {

  @InjectMocks
  private CardSubtypeMapper mapper;

  @Test
  void toEntity_shouldMapValidEntities() {
    Subtype subtype = SubtypeMock.createMockSubtype("Basic");
    Card card = CardMock.createBasicMockCard("card-123", "Pikachu");

    CardSubtype result = mapper.toEntity(subtype, card);

    assertThat(result).isNotNull();
    assertThat(result.getSubtype()).isEqualTo(subtype);
    assertThat(result.getCard()).isEqualTo(card);
    assertThat(result.getSubtype().getLabel()).isEqualTo("Basic");
    assertThat(result.getCard().getId()).isEqualTo("card-123");
    assertThat(result.getCard().getName()).isEqualTo("Pikachu");
  }

  @Test
  void toEntity_shouldMapEntitiesWithIds() {
    Subtype subtype = SubtypeMock.createMockSubtypeSavedInDb(42, "Stage 1");
    Card card = CardMock.createBasicMockCard("card-456", "Raichu");

    CardSubtype result = mapper.toEntity(subtype, card);

    assertThat(result).isNotNull();
    assertThat(result.getSubtype()).isEqualTo(subtype);
    assertThat(result.getSubtype().getId()).isEqualTo(42);
    assertThat(result.getSubtype().getLabel()).isEqualTo("Stage 1");
    assertThat(result.getCard()).isEqualTo(card);
    assertThat(result.getCard().getId()).isEqualTo("card-456");
  }

  @Test
  void toEntity_shouldReturnNullWithNullSubtype() {
    Card card = CardMock.createBasicMockCard("card-789", "Charizard");

    CardSubtype result = mapper.toEntity(null, card);

    assertThat(result).isNull();
  }

  @Test
  void toEntity_shouldReturnNullWithNullCard() {
    Subtype subtype = SubtypeMock.createMockSubtype("EX");

    CardSubtype result = mapper.toEntity(subtype, null);

    assertThat(result).isNull();
  }

  @Test
  void toEntity_shouldReturnNullWithBothInputsNull() {
    CardSubtype result = mapper.toEntity(null, null);

    assertThat(result).isNull();
  }
}