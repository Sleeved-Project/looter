package com.sleeved.looter.batch.reader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sleeved.looter.domain.entity.staging.StagingCard;
import com.sleeved.looter.domain.repository.staging.StagingCardRepository;
import com.sleeved.looter.infra.dto.CardDTO;
import com.sleeved.looter.mock.domain.StagingCardMock;
import com.sleeved.looter.mock.infra.CardDTOMock;

@ExtendWith(MockitoExtension.class)
class StagingCardToCardDTOReaderTest {

  @Mock
  private StagingCardRepository stagingCardRepository;

  private ObjectMapper objectMapper = new ObjectMapper();

  private StagingCardToCardDTOReader reader;

  @Test
  void read_shouldReturnCardDTOWhenStagingCardsExist() throws Exception {
    int cardCount = 2;
    List<StagingCard> mockCards = StagingCardMock.createMockStagingCardsList(cardCount);
    List<CardDTO> expectedCards = CardDTOMock.createMockCardDTOsList(cardCount);

    when(stagingCardRepository.findAll()).thenReturn(mockCards);

    reader = new StagingCardToCardDTOReader(stagingCardRepository, objectMapper);

    CardDTO firstCard = reader.read();
    CardDTO secondCard = reader.read();
    CardDTO thirdCard = reader.read();

    assertThat(firstCard).isNotNull();
    assertThat(firstCard.getId()).isEqualTo(expectedCards.get(0).getId());
    assertThat(firstCard.getName()).isEqualTo(expectedCards.get(0).getName());

    assertThat(secondCard).isNotNull();
    assertThat(secondCard.getId()).isEqualTo(expectedCards.get(1).getId());
    assertThat(secondCard.getName()).isEqualTo(expectedCards.get(1).getName());

    assertThat(thirdCard).isNull();
  }

  @Test
  void read_shouldReturnNullWhenNoMoreStagingCards() throws Exception {
    List<StagingCard> emptyCards = StagingCardMock.createMockStagingCardsList(0);
    when(stagingCardRepository.findAll()).thenReturn(emptyCards);

    reader = new StagingCardToCardDTOReader(stagingCardRepository, objectMapper);

    CardDTO result = reader.read();

    assertThat(result).isNull();
  }

  @Test
  void read_shouldProcessAllCardsInOrder() throws Exception {
    int cardCount = 5;
    List<StagingCard> mockCards = StagingCardMock.createMockStagingCardsList(cardCount);
    List<CardDTO> expectedCards = CardDTOMock.createMockCardDTOsList(cardCount);
    when(stagingCardRepository.findAll()).thenReturn(mockCards);

    reader = new StagingCardToCardDTOReader(stagingCardRepository, objectMapper);

    for (int i = 0; i < cardCount; i++) {
      CardDTO card = reader.read();
      assertThat(card).isNotNull();
      assertThat(card.getId()).isEqualTo(expectedCards.get(i).getId());
      assertThat(card.getName()).isEqualTo(expectedCards.get(i).getName());
    }

    CardDTO noMoreCard = reader.read();
    assertThat(noMoreCard).isNull();
  }
}
