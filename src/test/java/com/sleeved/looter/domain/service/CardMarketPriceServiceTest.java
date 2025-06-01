package com.sleeved.looter.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.entity.atlas.CardMarketPrice;
import com.sleeved.looter.domain.repository.atlas.CardMarketPriceRepository;
import com.sleeved.looter.mock.domain.CardMarketPriceMock;
import com.sleeved.looter.mock.domain.CardMock;

@ExtendWith(MockitoExtension.class)
class CardMarketPriceServiceTest {

  @Mock
  private CardMarketPriceRepository cardMarketPriceRepository;

  @InjectMocks
  private CardMarketPriceService cardMarketPriceService;

  @Test
  void getOrCreate_shouldReturnExistingPrice_whenPriceExists() {
    // Arrange
    LocalDate updatedAt = LocalDate.of(2023, 5, 15);
    Card card = CardMock.createBasicMockCard("card123", "Pikachu");

    CardMarketPrice inputPrice = CardMarketPriceMock.createBasicMockCardMarketPrice(
        "https://cardmarket.com/card123",
        updatedAt,
        card);

    CardMarketPrice existingPrice = CardMarketPriceMock.createMockCardMarketPriceSavedInDb(
        1L,
        "https://cardmarket.com/card123",
        updatedAt,
        card,
        10.0, 8.0, 9.5, 9.0, 11.0,
        12.0, 10.5, 11.5, 9.0,
        10.2, 9.8, 9.5,
        11.2, 10.8, 10.5);

    when(cardMarketPriceRepository.findByUpdatedAtAndCard(updatedAt, card))
        .thenReturn(Optional.of(existingPrice));

    // Act
    CardMarketPrice result = cardMarketPriceService.getOrCreate(inputPrice);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1L);
    assertThat(result.getUrl()).isEqualTo("https://cardmarket.com/card123");
    assertThat(result.getUpdatedAt()).isEqualTo(updatedAt);
    assertThat(result.getCard()).isEqualTo(card);
    assertThat(result.getAverageSellPrice()).isEqualTo(10.0);
    assertThat(result.getLowPrice()).isEqualTo(8.0);
    assertThat(result.getTrendPrice()).isEqualTo(9.5);

    verify(cardMarketPriceRepository).findByUpdatedAtAndCard(updatedAt, card);
    verify(cardMarketPriceRepository, never()).save(any(CardMarketPrice.class));
  }

  @Test
  void getOrCreate_shouldCreateAndReturnNewPrice_whenPriceDoesNotExist() {
    // Arrange
    LocalDate updatedAt = LocalDate.of(2023, 5, 15);
    Card card = CardMock.createBasicMockCard("card123", "Pikachu");

    CardMarketPrice inputPrice = CardMarketPriceMock.createBasicMockCardMarketPrice(
        "https://cardmarket.com/card123",
        updatedAt,
        card);

    CardMarketPrice savedPrice = CardMarketPriceMock.createMockCardMarketPriceSavedInDb(
        1L,
        "https://cardmarket.com/card123",
        updatedAt,
        card,
        10.0, 8.0, 9.5, 9.0, 11.0,
        12.0, 10.5, 11.5, 9.0,
        10.2, 9.8, 9.5,
        11.2, 10.8, 10.5);

    when(cardMarketPriceRepository.findByUpdatedAtAndCard(updatedAt, card))
        .thenReturn(Optional.empty());
    when(cardMarketPriceRepository.save(inputPrice)).thenReturn(savedPrice);

    // Act
    CardMarketPrice result = cardMarketPriceService.getOrCreate(inputPrice);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1L);
    assertThat(result.getUrl()).isEqualTo("https://cardmarket.com/card123");
    assertThat(result.getUpdatedAt()).isEqualTo(updatedAt);
    assertThat(result.getCard()).isEqualTo(card);
    assertThat(result.getAverageSellPrice()).isEqualTo(10.0);
    assertThat(result.getLowPrice()).isEqualTo(8.0);
    assertThat(result.getTrendPrice()).isEqualTo(9.5);

    verify(cardMarketPriceRepository).findByUpdatedAtAndCard(updatedAt, card);
    verify(cardMarketPriceRepository).save(inputPrice);
  }

  @Test
  void getOrCreate_shouldHandleDifferentDates() {
    // Arrange
    LocalDate updatedAtInput = LocalDate.of(2023, 5, 15);
    Card card = CardMock.createBasicMockCard("card123", "Pikachu");

    CardMarketPrice inputPrice = CardMarketPriceMock.createBasicMockCardMarketPrice(
        "https://cardmarket.com/card123",
        updatedAtInput,
        card);

    CardMarketPrice savedPrice = CardMarketPriceMock.createMockCardMarketPriceSavedInDb(
        1L,
        "https://cardmarket.com/card123",
        updatedAtInput,
        card,
        10.0, 8.0, 9.5, 9.0, 11.0,
        12.0, 10.5, 11.5, 9.0,
        10.2, 9.8, 9.5,
        11.2, 10.8, 10.5);

    when(cardMarketPriceRepository.findByUpdatedAtAndCard(updatedAtInput, card))
        .thenReturn(Optional.empty());
    when(cardMarketPriceRepository.save(inputPrice)).thenReturn(savedPrice);

    // Act
    CardMarketPrice result = cardMarketPriceService.getOrCreate(inputPrice);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1L);
    assertThat(result.getUrl()).isEqualTo("https://cardmarket.com/card123");
    assertThat(result.getUpdatedAt()).isEqualTo(updatedAtInput);
    assertThat(result.getCard()).isEqualTo(card);

    verify(cardMarketPriceRepository).findByUpdatedAtAndCard(updatedAtInput, card);
    verify(cardMarketPriceRepository).save(inputPrice);
  }

  @Test
  void getOrCreate_shouldHandleDifferentCards() {
    // Arrange
    LocalDate updatedAt = LocalDate.of(2023, 5, 15);
    Card card1 = CardMock.createBasicMockCard("card123", "Pikachu");
    Card card2 = CardMock.createBasicMockCard("card456", "Charizard");

    CardMarketPrice inputPrice = CardMarketPriceMock.createBasicMockCardMarketPrice(
        "https://cardmarket.com/card123",
        updatedAt,
        card1);

    CardMarketPrice savedPrice = CardMarketPriceMock.createMockCardMarketPriceSavedInDb(
        1L,
        "https://cardmarket.com/card123",
        updatedAt,
        card1,
        10.0, 8.0, 9.5, 9.0, 11.0,
        12.0, 10.5, 11.5, 9.0,
        10.2, 9.8, 9.5,
        11.2, 10.8, 10.5);

    when(cardMarketPriceRepository.findByUpdatedAtAndCard(updatedAt, card1))
        .thenReturn(Optional.empty());
    when(cardMarketPriceRepository.save(inputPrice)).thenReturn(savedPrice);

    // Act
    CardMarketPrice result = cardMarketPriceService.getOrCreate(inputPrice);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1L);
    assertThat(result.getUrl()).isEqualTo("https://cardmarket.com/card123");
    assertThat(result.getUpdatedAt()).isEqualTo(updatedAt);
    assertThat(result.getCard()).isEqualTo(card1);
    assertThat(result.getCard()).isNotEqualTo(card2);

    verify(cardMarketPriceRepository).findByUpdatedAtAndCard(updatedAt, card1);
    verify(cardMarketPriceRepository).save(inputPrice);
  }
}