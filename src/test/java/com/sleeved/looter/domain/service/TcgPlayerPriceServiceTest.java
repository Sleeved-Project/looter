package com.sleeved.looter.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.entity.atlas.TcgPlayerPrice;
import com.sleeved.looter.domain.entity.atlas.TcgPlayerReporting;
import com.sleeved.looter.domain.repository.atlas.TcgPlayerPriceRepository;
import com.sleeved.looter.mock.domain.CardMock;
import com.sleeved.looter.mock.domain.TcgPlayerPriceMock;
import com.sleeved.looter.mock.domain.TcgPlayerReportingMock;

@ExtendWith(MockitoExtension.class)
class TcgPlayerPriceServiceTest {

  @Mock
  private TcgPlayerPriceRepository tcgPlayerPriceRepository;

  @InjectMocks
  private TcgPlayerPriceService tcgPlayerPriceService;

  private Card card;
  private TcgPlayerReporting tcgPlayerReporting;
  private TcgPlayerPrice normalPrice;
  private TcgPlayerPrice holoPrice;

  @BeforeEach
  void setUp() {
    card = CardMock.createBasicMockCard("swsh1-25", "Pikachu");
    tcgPlayerReporting = TcgPlayerReportingMock.createMockTcgPlayerReportingSavedInDb(
        1L,
        "https://tcgplayer.com/card123",
        LocalDate.now(),
        card);

    normalPrice = TcgPlayerPriceMock.createMockTcgPlayerPrice(
        null,
        "normal",
        1.0,
        2.0,
        3.0,
        2.5,
        1.5,
        tcgPlayerReporting);

    holoPrice = TcgPlayerPriceMock.createMockTcgPlayerPrice(
        null,
        "holofoil",
        3.0,
        4.0,
        5.0,
        4.5,
        3.5,
        tcgPlayerReporting);
  }

  @Test
  void getOrCreate_shouldReturnExistingPrice_whenPriceExists() {
    // Given
    TcgPlayerPrice savedNormalPrice = TcgPlayerPriceMock.createMockTcgPlayerPriceSavedInDb(
        1L,
        "normal",
        1.0,
        2.0,
        3.0,
        2.5,
        1.5,
        tcgPlayerReporting);

    when(tcgPlayerPriceRepository.findByTypeAndTcgPlayerReporting("normal", tcgPlayerReporting))
        .thenReturn(Optional.of(savedNormalPrice));

    // When
    TcgPlayerPrice result = tcgPlayerPriceService.getOrCreate(normalPrice);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1L);
    assertThat(result.getType()).isEqualTo("normal");
    assertThat(result.getLow()).isEqualTo(1.0);
    assertThat(result.getMid()).isEqualTo(2.0);
    assertThat(result.getHigh()).isEqualTo(3.0);
    assertThat(result.getMarket()).isEqualTo(2.5);
    assertThat(result.getDirectLow()).isEqualTo(1.5);
    assertThat(result.getTcgPlayerReporting()).isEqualTo(tcgPlayerReporting);

    verify(tcgPlayerPriceRepository).findByTypeAndTcgPlayerReporting("normal", tcgPlayerReporting);
    verify(tcgPlayerPriceRepository, never()).save(any(TcgPlayerPrice.class));
  }

  @Test
  void getOrCreate_shouldCreateAndReturnNewPrice_whenPriceDoesNotExist() {
    // Given
    TcgPlayerPrice savedNormalPrice = TcgPlayerPriceMock.createMockTcgPlayerPriceSavedInDb(
        1L,
        "normal",
        1.0,
        2.0,
        3.0,
        2.5,
        1.5,
        tcgPlayerReporting);

    when(tcgPlayerPriceRepository.findByTypeAndTcgPlayerReporting("normal", tcgPlayerReporting))
        .thenReturn(Optional.empty());
    when(tcgPlayerPriceRepository.save(normalPrice)).thenReturn(savedNormalPrice);

    // When
    TcgPlayerPrice result = tcgPlayerPriceService.getOrCreate(normalPrice);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1L);
    assertThat(result.getType()).isEqualTo("normal");
    assertThat(result.getLow()).isEqualTo(1.0);
    assertThat(result.getMid()).isEqualTo(2.0);
    assertThat(result.getHigh()).isEqualTo(3.0);
    assertThat(result.getMarket()).isEqualTo(2.5);
    assertThat(result.getDirectLow()).isEqualTo(1.5);
    assertThat(result.getTcgPlayerReporting()).isEqualTo(tcgPlayerReporting);

    verify(tcgPlayerPriceRepository).findByTypeAndTcgPlayerReporting("normal", tcgPlayerReporting);
    verify(tcgPlayerPriceRepository).save(normalPrice);
  }

  @Test
  void getOrCreate_shouldHandleDifferentPriceTypes() {
    // Given
    TcgPlayerPrice savedNormalPrice = TcgPlayerPriceMock.createMockTcgPlayerPriceSavedInDb(
        1L,
        "normal",
        1.0,
        2.0,
        3.0,
        2.5,
        1.5,
        tcgPlayerReporting);

    TcgPlayerPrice savedHoloPrice = TcgPlayerPriceMock.createMockTcgPlayerPriceSavedInDb(
        2L,
        "holofoil",
        3.0,
        4.0,
        5.0,
        4.5,
        3.5,
        tcgPlayerReporting);

    when(tcgPlayerPriceRepository.findByTypeAndTcgPlayerReporting("normal", tcgPlayerReporting))
        .thenReturn(Optional.empty());
    when(tcgPlayerPriceRepository.findByTypeAndTcgPlayerReporting("holofoil", tcgPlayerReporting))
        .thenReturn(Optional.of(savedHoloPrice));
    when(tcgPlayerPriceRepository.save(normalPrice)).thenReturn(savedNormalPrice);

    // When
    TcgPlayerPrice normalResult = tcgPlayerPriceService.getOrCreate(normalPrice);
    TcgPlayerPrice holoResult = tcgPlayerPriceService.getOrCreate(holoPrice);

    // Then
    assertThat(normalResult).isNotNull();
    assertThat(normalResult.getId()).isEqualTo(1L);
    assertThat(normalResult.getType()).isEqualTo("normal");

    assertThat(holoResult).isNotNull();
    assertThat(holoResult.getId()).isEqualTo(2L);
    assertThat(holoResult.getType()).isEqualTo("holofoil");

    verify(tcgPlayerPriceRepository).findByTypeAndTcgPlayerReporting("normal", tcgPlayerReporting);
    verify(tcgPlayerPriceRepository).findByTypeAndTcgPlayerReporting("holofoil", tcgPlayerReporting);
    verify(tcgPlayerPriceRepository).save(normalPrice);
    verify(tcgPlayerPriceRepository, never()).save(holoPrice);
  }

  @Test
  void getOrCreate_shouldHandleDifferentReportings() {
    // Given
    Card card2 = CardMock.createBasicMockCard("swsh1-26", "Charizard");
    TcgPlayerReporting tcgPlayerReporting2 = TcgPlayerReportingMock.createMockTcgPlayerReportingSavedInDb(
        2L,
        "https://tcgplayer.com/card456",
        LocalDate.now(),
        card2);

    TcgPlayerPrice normalPriceForReporting2 = TcgPlayerPriceMock.createMockTcgPlayerPrice(
        null,
        "normal",
        10.0,
        20.0,
        30.0,
        25.0,
        15.0,
        tcgPlayerReporting2);

    TcgPlayerPrice savedNormalPrice = TcgPlayerPriceMock.createMockTcgPlayerPriceSavedInDb(
        1L,
        "normal",
        1.0,
        2.0,
        3.0,
        2.5,
        1.5,
        tcgPlayerReporting);

    TcgPlayerPrice savedNormalPriceForReporting2 = TcgPlayerPriceMock.createMockTcgPlayerPriceSavedInDb(
        3L,
        "normal",
        10.0,
        20.0,
        30.0,
        25.0,
        15.0,
        tcgPlayerReporting2);

    when(tcgPlayerPriceRepository.findByTypeAndTcgPlayerReporting("normal", tcgPlayerReporting))
        .thenReturn(Optional.of(savedNormalPrice));
    when(tcgPlayerPriceRepository.findByTypeAndTcgPlayerReporting("normal", tcgPlayerReporting2))
        .thenReturn(Optional.empty());
    when(tcgPlayerPriceRepository.save(normalPriceForReporting2))
        .thenReturn(savedNormalPriceForReporting2);

    // When
    TcgPlayerPrice result1 = tcgPlayerPriceService.getOrCreate(normalPrice);
    TcgPlayerPrice result2 = tcgPlayerPriceService.getOrCreate(normalPriceForReporting2);

    // Then
    assertThat(result1).isNotNull();
    assertThat(result1.getId()).isEqualTo(1L);
    assertThat(result1.getTcgPlayerReporting().getId()).isEqualTo(1L);
    assertThat(result1.getTcgPlayerReporting().getCard().getId()).isEqualTo("swsh1-25");

    assertThat(result2).isNotNull();
    assertThat(result2.getId()).isEqualTo(3L);
    assertThat(result2.getTcgPlayerReporting().getId()).isEqualTo(2L);
    assertThat(result2.getTcgPlayerReporting().getCard().getId()).isEqualTo("swsh1-26");

    verify(tcgPlayerPriceRepository).findByTypeAndTcgPlayerReporting("normal", tcgPlayerReporting);
    verify(tcgPlayerPriceRepository).findByTypeAndTcgPlayerReporting("normal", tcgPlayerReporting2);
    verify(tcgPlayerPriceRepository, never()).save(normalPrice);
    verify(tcgPlayerPriceRepository).save(normalPriceForReporting2);
  }
}