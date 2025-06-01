package com.sleeved.looter.batch.writer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.Chunk;

import com.sleeved.looter.common.util.Constantes;
import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.entity.atlas.CardMarketPrice;
import com.sleeved.looter.domain.entity.atlas.TcgPlayerReporting;
import com.sleeved.looter.domain.service.CardMarketPriceService;
import com.sleeved.looter.domain.service.ResistanceService;
import com.sleeved.looter.domain.service.SetService;
import com.sleeved.looter.domain.service.TcgPlayerReportingService;
import com.sleeved.looter.domain.service.WeaknessService;
import com.sleeved.looter.infra.dto.ReportingPriceEntitiesProcessedDTO;
import com.sleeved.looter.infra.service.LooterScrapingErrorHandler;
import com.sleeved.looter.mock.domain.CardMarketPriceMock;
import com.sleeved.looter.mock.domain.CardMock;
import com.sleeved.looter.mock.domain.TcgPlayerReportingMock;

@ExtendWith(MockitoExtension.class)
class ReportingPriceEntityWriterTest {

  @Mock
  private TcgPlayerReportingService tcgPlayerReportingService;

  @Mock
  private CardMarketPriceService cardMarketPriceService;

  @Mock
  private LooterScrapingErrorHandler looterScrapingErrorHandler;

  @Mock
  private SetService setService;

  @Mock
  private WeaknessService weaknessService;

  @Mock
  private ResistanceService resistanceService;

  @InjectMocks
  private ReportingPriceEntityWriter writer;

  @Test
  void write_shouldProcessEmptyChunk() throws Exception {
    Chunk<ReportingPriceEntitiesProcessedDTO> chunk = new Chunk<>(Collections.emptyList());

    writer.write(chunk);

    verifyNoInteractions(tcgPlayerReportingService, cardMarketPriceService, looterScrapingErrorHandler);
  }

  @Test
  void write_shouldProcessBothEntitiesWhenBothExist() throws Exception {
    Card card = CardMock.createBasicMockCard("card123", "Pikachu");
    LocalDate updatedAt = LocalDate.of(2023, 5, 15);

    TcgPlayerReporting tcgPlayerReporting = TcgPlayerReportingMock.createMockTcgPlayerReporting(
        null, "https://tcgplayer.com/card123", updatedAt, card);

    CardMarketPrice cardMarketPrice = CardMarketPriceMock.createBasicMockCardMarketPrice(
        "https://cardmarket.com/card123", updatedAt, card);

    ReportingPriceEntitiesProcessedDTO dto = new ReportingPriceEntitiesProcessedDTO();
    dto.setTcgPlayerReporting(tcgPlayerReporting);
    dto.setCardMarketPrice(cardMarketPrice);

    Chunk<ReportingPriceEntitiesProcessedDTO> chunk = new Chunk<>(List.of(dto));

    writer.write(chunk);

    verify(tcgPlayerReportingService).getOrCreate(tcgPlayerReporting);
    verify(cardMarketPriceService).getOrCreate(cardMarketPrice);
    verifyNoInteractions(looterScrapingErrorHandler);
  }

  @Test
  void write_shouldProcessOnlyTcgPlayerWhenCardMarketIsNull() throws Exception {
    Card card = CardMock.createBasicMockCard("card123", "Pikachu");
    LocalDate updatedAt = LocalDate.of(2023, 5, 15);

    TcgPlayerReporting tcgPlayerReporting = TcgPlayerReportingMock.createMockTcgPlayerReporting(
        null, "https://tcgplayer.com/card123", updatedAt, card);

    ReportingPriceEntitiesProcessedDTO dto = new ReportingPriceEntitiesProcessedDTO();
    dto.setTcgPlayerReporting(tcgPlayerReporting);
    dto.setCardMarketPrice(null);

    Chunk<ReportingPriceEntitiesProcessedDTO> chunk = new Chunk<>(List.of(dto));

    writer.write(chunk);

    verify(tcgPlayerReportingService).getOrCreate(tcgPlayerReporting);
    verify(cardMarketPriceService, never()).getOrCreate(any());
    verifyNoInteractions(looterScrapingErrorHandler);
  }

  @Test
  void write_shouldProcessOnlyCardMarketWhenTcgPlayerIsNull() throws Exception {
    Card card = CardMock.createBasicMockCard("card123", "Pikachu");
    LocalDate updatedAt = LocalDate.of(2023, 5, 15);

    CardMarketPrice cardMarketPrice = CardMarketPriceMock.createBasicMockCardMarketPrice(
        "https://cardmarket.com/card123", updatedAt, card);

    ReportingPriceEntitiesProcessedDTO dto = new ReportingPriceEntitiesProcessedDTO();
    dto.setTcgPlayerReporting(null);
    dto.setCardMarketPrice(cardMarketPrice);

    Chunk<ReportingPriceEntitiesProcessedDTO> chunk = new Chunk<>(List.of(dto));

    writer.write(chunk);

    verify(cardMarketPriceService).getOrCreate(cardMarketPrice);
    verify(tcgPlayerReportingService, never()).getOrCreate(any());
    verifyNoInteractions(looterScrapingErrorHandler);
  }

  @Test
  void write_shouldProcessMultipleItems() throws Exception {
    Card card1 = CardMock.createBasicMockCard("card123", "Pikachu");
    Card card2 = CardMock.createBasicMockCard("card456", "Charizard");
    LocalDate updatedAt = LocalDate.of(2023, 5, 15);

    TcgPlayerReporting tcgPlayerReporting1 = TcgPlayerReportingMock.createMockTcgPlayerReporting(
        null, "https://tcgplayer.com/card123", updatedAt, card1);
    CardMarketPrice cardMarketPrice1 = CardMarketPriceMock.createBasicMockCardMarketPrice(
        "https://cardmarket.com/card123", updatedAt, card1);

    TcgPlayerReporting tcgPlayerReporting2 = TcgPlayerReportingMock.createMockTcgPlayerReporting(
        null, "https://tcgplayer.com/card456", updatedAt, card2);
    CardMarketPrice cardMarketPrice2 = CardMarketPriceMock.createBasicMockCardMarketPrice(
        "https://cardmarket.com/card456", updatedAt, card2);

    ReportingPriceEntitiesProcessedDTO dto1 = new ReportingPriceEntitiesProcessedDTO();
    dto1.setTcgPlayerReporting(tcgPlayerReporting1);
    dto1.setCardMarketPrice(cardMarketPrice1);

    ReportingPriceEntitiesProcessedDTO dto2 = new ReportingPriceEntitiesProcessedDTO();
    dto2.setTcgPlayerReporting(tcgPlayerReporting2);
    dto2.setCardMarketPrice(cardMarketPrice2);

    Chunk<ReportingPriceEntitiesProcessedDTO> chunk = new Chunk<>(Arrays.asList(dto1, dto2));

    writer.write(chunk);

    verify(tcgPlayerReportingService).getOrCreate(tcgPlayerReporting1);
    verify(tcgPlayerReportingService).getOrCreate(tcgPlayerReporting2);
    verify(cardMarketPriceService).getOrCreate(cardMarketPrice1);
    verify(cardMarketPriceService).getOrCreate(cardMarketPrice2);
    verifyNoInteractions(looterScrapingErrorHandler);
  }

  @Test
  void write_shouldHandleExceptionForTcgPlayerReportingService() throws Exception {
    Card card = CardMock.createBasicMockCard("card123", "Pikachu");
    LocalDate updatedAt = LocalDate.of(2023, 5, 15);

    TcgPlayerReporting tcgPlayerReporting = TcgPlayerReportingMock.createMockTcgPlayerReporting(
        null, "https://tcgplayer.com/card123", updatedAt, card);

    CardMarketPrice cardMarketPrice = CardMarketPriceMock.createBasicMockCardMarketPrice(
        "https://cardmarket.com/card123", updatedAt, card);

    ReportingPriceEntitiesProcessedDTO dto = new ReportingPriceEntitiesProcessedDTO();
    dto.setTcgPlayerReporting(tcgPlayerReporting);
    dto.setCardMarketPrice(cardMarketPrice);

    Exception serviceException = new RuntimeException("Service error");
    when(cardMarketPriceService.getOrCreate(cardMarketPrice)).thenReturn(cardMarketPrice);
    when(tcgPlayerReportingService.getOrCreate(tcgPlayerReporting)).thenThrow(serviceException);

    String formattedItemValue = "REPORTING_PRICE_ITEM: ReportingPriceEntitiesProcessedDTO(...)";
    when(looterScrapingErrorHandler.formatErrorItem(
        eq(Constantes.REPORTING_PRICE_ITEM),
        anyString())).thenReturn(formattedItemValue);

    Chunk<ReportingPriceEntitiesProcessedDTO> chunk = new Chunk<>(List.of(dto));

    writer.write(chunk);

    verify(looterScrapingErrorHandler).formatErrorItem(
        eq(Constantes.REPORTING_PRICE_ITEM),
        anyString());

    verify(looterScrapingErrorHandler).handle(
        eq(serviceException),
        eq(Constantes.REPORTING_PRICE_ENTITIES_WRITER_CONTEXT),
        eq(Constantes.WRITE_ACTION),
        eq(formattedItemValue));
  }

  @Test
  void write_shouldContinueProcessingAfterException() throws Exception {
    Card card1 = CardMock.createBasicMockCard("card123", "Pikachu");
    Card card2 = CardMock.createBasicMockCard("card456", "Charizard");
    LocalDate updatedAt = LocalDate.of(2023, 5, 15);

    TcgPlayerReporting tcgPlayerReporting1 = TcgPlayerReportingMock.createMockTcgPlayerReporting(
        null, "https://tcgplayer.com/card123", updatedAt, card1);
    CardMarketPrice cardMarketPrice1 = CardMarketPriceMock.createBasicMockCardMarketPrice(
        "https://cardmarket.com/card123", updatedAt, card1);

    TcgPlayerReporting tcgPlayerReporting2 = TcgPlayerReportingMock.createMockTcgPlayerReporting(
        null, "https://tcgplayer.com/card456", updatedAt, card2);
    CardMarketPrice cardMarketPrice2 = CardMarketPriceMock.createBasicMockCardMarketPrice(
        "https://cardmarket.com/card456", updatedAt, card2);

    ReportingPriceEntitiesProcessedDTO dto1 = new ReportingPriceEntitiesProcessedDTO();
    dto1.setTcgPlayerReporting(tcgPlayerReporting1);
    dto1.setCardMarketPrice(cardMarketPrice1);

    ReportingPriceEntitiesProcessedDTO dto2 = new ReportingPriceEntitiesProcessedDTO();
    dto2.setTcgPlayerReporting(tcgPlayerReporting2);
    dto2.setCardMarketPrice(cardMarketPrice2);

    Exception serviceException = new RuntimeException("Service error");
    when(cardMarketPriceService.getOrCreate(cardMarketPrice1)).thenThrow(serviceException);
    when(cardMarketPriceService.getOrCreate(cardMarketPrice2)).thenReturn(cardMarketPrice2);
    when(tcgPlayerReportingService.getOrCreate(tcgPlayerReporting2)).thenReturn(tcgPlayerReporting2);

    when(looterScrapingErrorHandler.formatErrorItem(anyString(), anyString()))
        .thenReturn("Formatted error item");

    Chunk<ReportingPriceEntitiesProcessedDTO> chunk = new Chunk<>(Arrays.asList(dto1, dto2));

    writer.write(chunk);

    verify(looterScrapingErrorHandler).handle(
        any(Exception.class),
        eq(Constantes.REPORTING_PRICE_ENTITIES_WRITER_CONTEXT),
        eq(Constantes.WRITE_ACTION),
        anyString());

    verify(tcgPlayerReportingService, never()).getOrCreate(tcgPlayerReporting1);
    verify(tcgPlayerReportingService).getOrCreate(tcgPlayerReporting2);
    verify(cardMarketPriceService).getOrCreate(cardMarketPrice2);
  }

  @Test
  void write_shouldUseCorrectConstantsForErrorHandling() throws Exception {
    Card card = CardMock.createBasicMockCard("card123", "Pikachu");
    LocalDate updatedAt = LocalDate.of(2023, 5, 15);

    TcgPlayerReporting tcgPlayerReporting = TcgPlayerReportingMock.createMockTcgPlayerReporting(
        null, "https://tcgplayer.com/card123", updatedAt, card);

    CardMarketPrice cardMarketPrice = CardMarketPriceMock.createBasicMockCardMarketPrice(
        "https://cardmarket.com/card123", updatedAt, card);

    ReportingPriceEntitiesProcessedDTO dto = new ReportingPriceEntitiesProcessedDTO();
    dto.setTcgPlayerReporting(tcgPlayerReporting);
    dto.setCardMarketPrice(cardMarketPrice);

    Exception serviceException = new RuntimeException("Service error");
    when(cardMarketPriceService.getOrCreate(cardMarketPrice)).thenThrow(serviceException);

    when(looterScrapingErrorHandler.formatErrorItem(anyString(), anyString()))
        .thenReturn("Formatted error item");

    Chunk<ReportingPriceEntitiesProcessedDTO> chunk = new Chunk<>(List.of(dto));

    writer.write(chunk);

    verify(looterScrapingErrorHandler).formatErrorItem(
        eq(Constantes.REPORTING_PRICE_ITEM),
        anyString());

    verify(looterScrapingErrorHandler).handle(
        eq(serviceException),
        eq(Constantes.REPORTING_PRICE_ENTITIES_WRITER_CONTEXT),
        eq(Constantes.WRITE_ACTION),
        anyString());
  }
}