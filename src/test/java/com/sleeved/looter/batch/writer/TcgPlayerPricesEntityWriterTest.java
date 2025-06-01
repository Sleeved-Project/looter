package com.sleeved.looter.batch.writer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.Chunk;

import com.sleeved.looter.common.util.Constantes;
import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.entity.atlas.TcgPlayerPrice;
import com.sleeved.looter.domain.entity.atlas.TcgPlayerReporting;
import com.sleeved.looter.domain.service.TcgPlayerPriceService;
import com.sleeved.looter.infra.dto.TcgPlayerPricesEntitiesProcessedDTO;
import com.sleeved.looter.infra.service.LooterScrapingErrorHandler;
import com.sleeved.looter.mock.domain.CardMock;
import com.sleeved.looter.mock.domain.TcgPlayerPriceMock;
import com.sleeved.looter.mock.domain.TcgPlayerReportingMock;

@ExtendWith(MockitoExtension.class)
class TcgPlayerPricesEntityWriterTest {

  @Mock
  private TcgPlayerPriceService tcgPlayerPriceService;

  @Mock
  private LooterScrapingErrorHandler looterScrapingErrorHandler;

  @InjectMocks
  private TcgPlayerPricesEntityWriter writer;

  private Card card;
  private TcgPlayerReporting tcgPlayerReporting;
  private TcgPlayerPrice normalPrice;
  private TcgPlayerPrice holoPrice;
  private TcgPlayerPricesEntitiesProcessedDTO dto;

  @BeforeEach
  void setUp() {
    card = CardMock.createBasicMockCard("swsh1-25", "Pikachu");
    tcgPlayerReporting = TcgPlayerReportingMock.createMockTcgPlayerReportingSavedInDb(
        1L,
        "https://tcgplayer.com/swsh1-25",
        LocalDate.now(),
        card);

    normalPrice = TcgPlayerPriceMock.createMockTcgPlayerPriceSavedInDb(
        1L,
        "normal",
        1.0,
        2.0,
        3.0,
        2.5,
        1.5,
        tcgPlayerReporting);

    holoPrice = TcgPlayerPriceMock.createMockTcgPlayerPriceSavedInDb(
        2L,
        "holofoil",
        3.0,
        4.0,
        5.0,
        4.5,
        3.5,
        tcgPlayerReporting);

    dto = new TcgPlayerPricesEntitiesProcessedDTO();
    dto.setTcgPlayerPrices(new ArrayList<>(Arrays.asList(normalPrice, holoPrice)));
  }

  @Test
  void write_shouldProcessEmptyChunk() throws Exception {
    Chunk<TcgPlayerPricesEntitiesProcessedDTO> chunk = new Chunk<>(Collections.emptyList());

    writer.write(chunk);

    verifyNoInteractions(tcgPlayerPriceService, looterScrapingErrorHandler);
  }

  @Test
  void write_shouldProcessAllPricesInDTO() throws Exception {
    Chunk<TcgPlayerPricesEntitiesProcessedDTO> chunk = new Chunk<>(List.of(dto));
    when(tcgPlayerPriceService.getOrCreate(normalPrice)).thenReturn(normalPrice);
    when(tcgPlayerPriceService.getOrCreate(holoPrice)).thenReturn(holoPrice);

    writer.write(chunk);

    verify(tcgPlayerPriceService).getOrCreate(normalPrice);
    verify(tcgPlayerPriceService).getOrCreate(holoPrice);
    verifyNoInteractions(looterScrapingErrorHandler);
  }

  @Test
  void write_shouldProcessMultipleDTOs() throws Exception {
    Card card2 = CardMock.createBasicMockCard("swsh1-26", "Charizard");
    TcgPlayerReporting tcgPlayerReporting2 = TcgPlayerReportingMock.createMockTcgPlayerReportingSavedInDb(
        2L,
        "https://tcgplayer.com/swsh1-26",
        LocalDate.now(),
        card2);

    TcgPlayerPrice normalPrice2 = TcgPlayerPriceMock.createMockTcgPlayerPriceSavedInDb(
        3L,
        "normal",
        5.0,
        7.0,
        9.0,
        7.5,
        5.5,
        tcgPlayerReporting2);

    TcgPlayerPrice holoPrice2 = TcgPlayerPriceMock.createMockTcgPlayerPriceSavedInDb(
        4L,
        "holofoil",
        10.0,
        15.0,
        20.0,
        17.5,
        12.5,
        tcgPlayerReporting2);

    TcgPlayerPricesEntitiesProcessedDTO dto2 = new TcgPlayerPricesEntitiesProcessedDTO();
    dto2.setTcgPlayerPrices(new ArrayList<>(Arrays.asList(normalPrice2, holoPrice2)));

    Chunk<TcgPlayerPricesEntitiesProcessedDTO> chunk = new Chunk<>(Arrays.asList(dto, dto2));

    when(tcgPlayerPriceService.getOrCreate(any(TcgPlayerPrice.class))).thenReturn(null);

    writer.write(chunk);

    verify(tcgPlayerPriceService).getOrCreate(normalPrice);
    verify(tcgPlayerPriceService).getOrCreate(holoPrice);
    verify(tcgPlayerPriceService).getOrCreate(normalPrice2);
    verify(tcgPlayerPriceService).getOrCreate(holoPrice2);
    verifyNoInteractions(looterScrapingErrorHandler);
  }

  @Test
  void write_shouldContinueProcessingAfterExceptionInOneDTO() throws Exception {
    TcgPlayerPricesEntitiesProcessedDTO problemDto = new TcgPlayerPricesEntitiesProcessedDTO();
    problemDto.setTcgPlayerPrices(new ArrayList<>(Arrays.asList(normalPrice)));

    TcgPlayerPricesEntitiesProcessedDTO goodDto = new TcgPlayerPricesEntitiesProcessedDTO();
    goodDto.setTcgPlayerPrices(new ArrayList<>(Arrays.asList(holoPrice)));

    Chunk<TcgPlayerPricesEntitiesProcessedDTO> chunk = new Chunk<>(Arrays.asList(problemDto, goodDto));

    Exception serviceException = new RuntimeException("Service error");
    when(tcgPlayerPriceService.getOrCreate(normalPrice)).thenThrow(serviceException);
    when(tcgPlayerPriceService.getOrCreate(holoPrice)).thenReturn(holoPrice);

    when(looterScrapingErrorHandler.formatErrorItem(anyString(), anyString()))
        .thenReturn("Formatted error item");

    doNothing().when(looterScrapingErrorHandler).handle(
        any(Exception.class),
        anyString(),
        anyString(),
        anyString());

    writer.write(chunk);

    verify(looterScrapingErrorHandler).formatErrorItem(
        eq(Constantes.SETS_WEAKNESS_RESISTANCE_CARD_ENTITIES_ITEM),
        anyString());

    verify(looterScrapingErrorHandler).handle(
        eq(serviceException),
        eq(Constantes.SETS_WEAKNESS_RESISTANCE_ENTITIES_WRITER_CONTEXT),
        eq(Constantes.WRITE_ACTION),
        anyString());

    verify(tcgPlayerPriceService).getOrCreate(normalPrice);
    verify(tcgPlayerPriceService).getOrCreate(holoPrice);
  }

  @Test
  void write_shouldHandleNullPricesList() throws Exception {
    TcgPlayerPricesEntitiesProcessedDTO nullPricesDto = new TcgPlayerPricesEntitiesProcessedDTO();
    nullPricesDto.setTcgPlayerPrices(null);

    Chunk<TcgPlayerPricesEntitiesProcessedDTO> chunk = new Chunk<>(List.of(nullPricesDto));

    writer.write(chunk);

    verify(looterScrapingErrorHandler).formatErrorItem(
        eq(Constantes.SETS_WEAKNESS_RESISTANCE_CARD_ENTITIES_ITEM),
        anyString());

    verifyNoMoreInteractions(tcgPlayerPriceService);
  }

  @Test
  void write_shouldHandleEmptyPricesList() throws Exception {
    TcgPlayerPricesEntitiesProcessedDTO emptyPricesDto = new TcgPlayerPricesEntitiesProcessedDTO();
    emptyPricesDto.setTcgPlayerPrices(new ArrayList<>());

    Chunk<TcgPlayerPricesEntitiesProcessedDTO> chunk = new Chunk<>(List.of(emptyPricesDto));

    writer.write(chunk);

    verifyNoInteractions(tcgPlayerPriceService, looterScrapingErrorHandler);
  }

  @Test
  void write_shouldUseCorrectConstantsForErrorHandling() throws Exception {
    Chunk<TcgPlayerPricesEntitiesProcessedDTO> chunk = new Chunk<>(List.of(dto));

    Exception serviceException = new RuntimeException("Service error");
    when(tcgPlayerPriceService.getOrCreate(normalPrice)).thenThrow(serviceException);

    when(looterScrapingErrorHandler.formatErrorItem(anyString(), anyString()))
        .thenReturn("Formatted error item");

    doNothing().when(looterScrapingErrorHandler).handle(
        any(Exception.class),
        anyString(),
        anyString(),
        anyString());

    writer.write(chunk);

    verify(looterScrapingErrorHandler).formatErrorItem(
        eq(Constantes.SETS_WEAKNESS_RESISTANCE_CARD_ENTITIES_ITEM),
        anyString());

    verify(looterScrapingErrorHandler).handle(
        eq(serviceException),
        eq(Constantes.SETS_WEAKNESS_RESISTANCE_ENTITIES_WRITER_CONTEXT),
        eq(Constantes.WRITE_ACTION),
        anyString());
  }
}