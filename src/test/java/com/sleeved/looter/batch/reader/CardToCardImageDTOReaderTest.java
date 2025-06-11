package com.sleeved.looter.batch.reader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.repository.atlas.CardRepository;
import com.sleeved.looter.infra.dto.CardImageDTO;
import com.sleeved.looter.infra.service.LooterScrapingErrorHandler;
import com.sleeved.looter.mock.domain.CardMock;

@ExtendWith(MockitoExtension.class)
class CardToCardImageDTOReaderTest {

  @Mock
  private CardRepository cardRepository;

  @Mock
  private LooterScrapingErrorHandler looterScrapingErrorHandler;

  private CardToCardImageDTOReader reader;

  @Test
  void read_shouldReturnCardImageDTOWhenCardsWithImagesExist() throws Exception {
    int cardCount = 2;
    List<Card> mockCards = CardMock.createMockCardsWithImage(cardCount);
    when(cardRepository.findByImageLargeIsNotNull()).thenReturn(mockCards);

    reader = new CardToCardImageDTOReader(cardRepository, looterScrapingErrorHandler);
    CardImageDTO firstCard = reader.read();
    CardImageDTO secondCard = reader.read();
    CardImageDTO thirdCard = reader.read();

    assertThat(firstCard).isNotNull();
    assertThat(firstCard.getCardId()).isEqualTo("card-1");
    assertThat(firstCard.getImageUrl()).isEqualTo("http://example.com/card1.jpg");

    assertThat(secondCard).isNotNull();
    assertThat(secondCard.getCardId()).isEqualTo("card-2");
    assertThat(secondCard.getImageUrl()).isEqualTo("http://example.com/card2.jpg");

    assertThat(thirdCard).isNull(); // No more cards
  }

  @Test
  void read_shouldReturnNullWhenNoCards() throws Exception {
    when(cardRepository.findByImageLargeIsNotNull()).thenReturn(Collections.emptyList());

    reader = new CardToCardImageDTOReader(cardRepository, looterScrapingErrorHandler);
    CardImageDTO result = reader.read();

    assertThat(result).isNull();
  }

  @Test
  void read_shouldSkipCardsWithoutImages() throws Exception {
    Card cardWithImage = CardMock.createMockCardsWithImage(1).get(0);
    Card cardWithoutImage = CardMock.createMockCardWithoutImage();
    Card secondCardWithImage = new Card();
    secondCardWithImage.setId("card-2");
    secondCardWithImage.setImageLarge("http://example.com/card2.jpg");

    List<Card> mixedCards = Arrays.asList(cardWithImage, cardWithoutImage, secondCardWithImage);
    when(cardRepository.findByImageLargeIsNotNull()).thenReturn(mixedCards);

    reader = new CardToCardImageDTOReader(cardRepository, looterScrapingErrorHandler);

    CardImageDTO firstResult = reader.read();
    assertThat(firstResult).isNotNull();
    assertThat(firstResult.getCardId()).isEqualTo("card-1");

    CardImageDTO secondResult = reader.read();
    assertThat(secondResult).isNotNull();
    assertThat(secondResult.getCardId()).isEqualTo("card-2");

    CardImageDTO thirdResult = reader.read();
    assertThat(thirdResult).isNull();
  }
}