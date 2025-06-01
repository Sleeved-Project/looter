package com.sleeved.looter.batch.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sleeved.looter.common.util.Constantes;
import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.entity.atlas.CardMarketPrice;
import com.sleeved.looter.domain.entity.atlas.TcgPlayerReporting;
import com.sleeved.looter.domain.service.CardService;
import com.sleeved.looter.infra.dto.CardMarketDTO;
import com.sleeved.looter.infra.dto.CardPriceDTO;
import com.sleeved.looter.infra.dto.ReportingPriceEntitiesProcessedDTO;
import com.sleeved.looter.infra.dto.TcgPlayerDTO;
import com.sleeved.looter.infra.mapper.CardMarketPriceMapper;
import com.sleeved.looter.infra.mapper.TcgPlayerReportingMapper;
import com.sleeved.looter.infra.service.LooterScrapingErrorHandler;
import com.sleeved.looter.mock.domain.CardMarketPriceMock;
import com.sleeved.looter.mock.domain.CardMock;
import com.sleeved.looter.mock.domain.TcgPlayerReportingMock;
import com.sleeved.looter.mock.infra.CardMarketDTOMock;
import com.sleeved.looter.mock.infra.CardPriceDTOMock;
import com.sleeved.looter.mock.infra.TcgPlayerDTOMock;

@ExtendWith(MockitoExtension.class)
class CardPriceDTOToReportingPriceEntityCardProcessorTest {

  @Mock
  private CardService cardService;

  @Mock
  private TcgPlayerReportingMapper tcgPlayerReportingMapper;

  @Mock
  private CardMarketPriceMapper cardMarketPriceMapper;

  @Mock
  private LooterScrapingErrorHandler looterScrapingErrorHandler;

  @InjectMocks
  private CardPriceDTOToReportingPriceEntityCardProcessor processor;

  @Captor
  private ArgumentCaptor<String> errorMessageCaptor;

  @Test
  void process_shouldReturnCorrectDTO_whenBothPricesExist() throws Exception {
    // Arrange
    String cardId = "base1-4";
    String cardName = "Charizard";

    // Créer des DTO
    TcgPlayerDTO tcgPlayerDTO = TcgPlayerDTOMock.createMockTcgPlayerDTO(
        "https://tcgplayer.com/base1-4",
        "2023/05/15");

    CardMarketDTO cardMarketDTO = CardMarketDTOMock.createMockCardMarketDTO(
        "https://cardmarket.com/base1-4",
        "2023/05/15");

    CardPriceDTO cardPriceDTO = CardPriceDTOMock.createMockCardPriceDTO(
        cardId,
        cardName,
        tcgPlayerDTO,
        cardMarketDTO);

    // Créer des entités
    Card card = CardMock.createBasicMockCard(cardId, cardName);
    TcgPlayerReporting tcgPlayerReporting = TcgPlayerReportingMock.createMockTcgPlayerReporting(
        null,
        "https://tcgplayer.com/base1-4",
        java.time.LocalDate.of(2023, 5, 15),
        card);

    CardMarketPrice cardMarketPrice = CardMarketPriceMock.createBasicMockCardMarketPrice(
        "https://cardmarket.com/base1-4",
        java.time.LocalDate.of(2023, 5, 15),
        card);

    // Configurer les mocks
    when(cardService.getById(cardId)).thenReturn(card);
    when(tcgPlayerReportingMapper.toEntity(tcgPlayerDTO, card)).thenReturn(tcgPlayerReporting);
    when(cardMarketPriceMapper.toEntity(cardMarketDTO, card)).thenReturn(cardMarketPrice);

    // Act
    ReportingPriceEntitiesProcessedDTO result = processor.process(cardPriceDTO);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getTcgPlayerReporting()).isEqualTo(tcgPlayerReporting);
    assertThat(result.getCardMarketPrice()).isEqualTo(cardMarketPrice);

    verify(cardService).getById(cardId);
    verify(tcgPlayerReportingMapper).toEntity(tcgPlayerDTO, card);
    verify(cardMarketPriceMapper).toEntity(cardMarketDTO, card);
  }

  @Test
  void process_shouldReturnCorrectDTO_whenOnlyTcgPlayerExists() throws Exception {
    // Arrange
    String cardId = "base1-4";
    String cardName = "Charizard";

    // Créer des DTO
    TcgPlayerDTO tcgPlayerDTO = TcgPlayerDTOMock.createMockTcgPlayerDTO(
        "https://tcgplayer.com/base1-4",
        "2023/05/15");

    CardPriceDTO cardPriceDTO = CardPriceDTOMock.createMockCardPriceDTO(
        cardId,
        cardName,
        tcgPlayerDTO,
        null);

    // Créer des entités
    Card card = CardMock.createBasicMockCard(cardId, cardName);
    TcgPlayerReporting tcgPlayerReporting = TcgPlayerReportingMock.createMockTcgPlayerReporting(
        null,
        "https://tcgplayer.com/base1-4",
        java.time.LocalDate.of(2023, 5, 15),
        card);

    // Configurer les mocks
    when(cardService.getById(cardId)).thenReturn(card);
    when(tcgPlayerReportingMapper.toEntity(tcgPlayerDTO, card)).thenReturn(tcgPlayerReporting);
    when(cardMarketPriceMapper.toEntity(null, card)).thenReturn(null);

    // Act
    ReportingPriceEntitiesProcessedDTO result = processor.process(cardPriceDTO);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getTcgPlayerReporting()).isEqualTo(tcgPlayerReporting);
    assertThat(result.getCardMarketPrice()).isNull();

    verify(cardService).getById(cardId);
    verify(tcgPlayerReportingMapper).toEntity(tcgPlayerDTO, card);
    verify(cardMarketPriceMapper).toEntity(null, card);
  }

  @Test
  void process_shouldReturnCorrectDTO_whenOnlyCardMarketExists() throws Exception {
    // Arrange
    String cardId = "base1-4";
    String cardName = "Charizard";

    // Créer des DTO
    CardMarketDTO cardMarketDTO = CardMarketDTOMock.createMockCardMarketDTO(
        "https://cardmarket.com/base1-4",
        "2023/05/15");

    CardPriceDTO cardPriceDTO = CardPriceDTOMock.createMockCardPriceDTO(
        cardId,
        cardName,
        null,
        cardMarketDTO);

    // Créer des entités
    Card card = CardMock.createBasicMockCard(cardId, cardName);
    CardMarketPrice cardMarketPrice = CardMarketPriceMock.createBasicMockCardMarketPrice(
        "https://cardmarket.com/base1-4",
        java.time.LocalDate.of(2023, 5, 15),
        card);

    // Configurer les mocks
    when(cardService.getById(cardId)).thenReturn(card);
    when(tcgPlayerReportingMapper.toEntity(null, card)).thenReturn(null);
    when(cardMarketPriceMapper.toEntity(cardMarketDTO, card)).thenReturn(cardMarketPrice);

    // Act
    ReportingPriceEntitiesProcessedDTO result = processor.process(cardPriceDTO);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getTcgPlayerReporting()).isNull();
    assertThat(result.getCardMarketPrice()).isEqualTo(cardMarketPrice);

    verify(cardService).getById(cardId);
    verify(tcgPlayerReportingMapper).toEntity(null, card);
    verify(cardMarketPriceMapper).toEntity(cardMarketDTO, card);
  }

  @Test
  void process_shouldReturnNull_whenBothPricesAreNull() throws Exception {
    // Arrange
    String cardId = "base1-4";
    String cardName = "Charizard";

    // Créer un DTO sans prix
    CardPriceDTO cardPriceDTO = CardPriceDTOMock.createMockCardPriceDTO(
        cardId,
        cardName,
        null,
        null);

    // Créer une entité
    Card card = CardMock.createBasicMockCard(cardId, cardName);

    // Configurer les mocks
    when(cardService.getById(cardId)).thenReturn(card);
    when(tcgPlayerReportingMapper.toEntity(null, card)).thenReturn(null);
    when(cardMarketPriceMapper.toEntity(null, card)).thenReturn(null);

    // Act
    ReportingPriceEntitiesProcessedDTO result = processor.process(cardPriceDTO);

    // Assert
    assertThat(result).isNull();

    verify(cardService).getById(cardId);
    verify(tcgPlayerReportingMapper).toEntity(null, card);
    verify(cardMarketPriceMapper).toEntity(null, card);
  }

  @Test
  void process_shouldReturnNullAndLogError_whenExceptionOccurs() throws Exception {
    // Arrange
    String cardId = "base1-4";
    String cardName = "Charizard";

    CardPriceDTO cardPriceDTO = CardPriceDTOMock.createBasicMockCardPriceDTO(cardId, cardName);

    // Simuler une exception
    when(cardService.getById(cardId)).thenThrow(new RuntimeException("Card not found"));
    doNothing().when(looterScrapingErrorHandler).handle(
        any(Exception.class),
        anyString(),
        anyString(),
        anyString());
    when(looterScrapingErrorHandler.formatErrorItem(anyString(), anyString()))
        .thenReturn("Formatted error item");

    // Act
    ReportingPriceEntitiesProcessedDTO result = processor.process(cardPriceDTO);

    // Assert
    assertThat(result).isNull();

    verify(cardService).getById(cardId);
    verify(looterScrapingErrorHandler).formatErrorItem(
        Constantes.CARD_PRICE_DTO_ITEM,
        cardPriceDTO.toString());
    verify(looterScrapingErrorHandler).handle(
        any(Exception.class),
        anyString(),
        anyString(),
        anyString());
  }
}