package com.sleeved.looter.batch.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sleeved.looter.common.util.Constantes;
import com.sleeved.looter.domain.entity.atlas.Card;
import com.sleeved.looter.domain.entity.atlas.TcgPlayerPrice;
import com.sleeved.looter.domain.entity.atlas.TcgPlayerReporting;
import com.sleeved.looter.domain.service.CardService;
import com.sleeved.looter.domain.service.TcgPlayerReportingService;
import com.sleeved.looter.infra.dto.CardPriceDTO;
import com.sleeved.looter.infra.dto.TcgPlayerDTO;
import com.sleeved.looter.infra.dto.TcgPlayerPriceDTO;
import com.sleeved.looter.infra.dto.TcgPlayerPricesEntitiesProcessedDTO;
import com.sleeved.looter.infra.mapper.TcgPlayerReportingMapper;
import com.sleeved.looter.infra.processor.TcgPlayerPriceProcessor;
import com.sleeved.looter.infra.service.LooterScrapingErrorHandler;
import com.sleeved.looter.mock.domain.CardMock;
import com.sleeved.looter.mock.domain.TcgPlayerPriceMock;
import com.sleeved.looter.mock.domain.TcgPlayerReportingMock;
import com.sleeved.looter.mock.infra.CardPriceDTOMock;
import com.sleeved.looter.mock.infra.TcgPlayerDTOMock;
import com.sleeved.looter.mock.infra.TcgPlayerPriceDTOMock;

@ExtendWith(MockitoExtension.class)
class CardPriceDTOToTcgPlayerPricesEntityCardProcessorTest {

  @Mock
  private CardService cardService;

  @Mock
  private TcgPlayerReportingMapper tcgPlayerReportingMapper;

  @Mock
  private TcgPlayerReportingService tcgPlayerReportingService;

  @Mock
  private TcgPlayerPriceProcessor tcgPlayerPriceProcessor;

  @Mock
  private LooterScrapingErrorHandler looterScrapingErrorHandler;

  @InjectMocks
  private CardPriceDTOToTcgPlayerPricesEntityCardProcessor processor;

  @Captor
  private ArgumentCaptor<String> errorMessageCaptor;

  private Card card;
  private TcgPlayerReporting tcgPlayerReporting;
  private CardPriceDTO cardPriceDTO;
  private TcgPlayerDTO tcgPlayerDTO;
  private Map<String, TcgPlayerPriceDTO> pricesMap;
  private TcgPlayerPrice normalPrice;
  private TcgPlayerPrice holoPrice;

  @BeforeEach
  void setUp() {
    card = CardMock.createBasicMockCard("swsh1-25", "Pikachu");

    tcgPlayerReporting = TcgPlayerReportingMock.createMockTcgPlayerReportingSavedInDb(
        1L,
        "https://tcgplayer.com/swsh1-25",
        LocalDate.of(2023, 5, 15),
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

    TcgPlayerPriceDTO normalPriceDTO = TcgPlayerPriceDTOMock.createMockTcgPlayerPriceDTO(
        1.0, 2.0, 3.0, 2.5, 1.5);

    TcgPlayerPriceDTO holoPriceDTO = TcgPlayerPriceDTOMock.createMockTcgPlayerPriceDTO(
        3.0, 4.0, 5.0, 4.5, 3.5);

    pricesMap = new HashMap<>();
    pricesMap.put("normal", normalPriceDTO);
    pricesMap.put("holofoil", holoPriceDTO);

    tcgPlayerDTO = TcgPlayerDTOMock.createMockTcgPlayerDTO(
        "https://tcgplayer.com/swsh1-25",
        "2023/05/15");
    tcgPlayerDTO.setPrices(pricesMap);

    cardPriceDTO = CardPriceDTOMock.createMockCardPriceDTO(
        "swsh1-25",
        "Pikachu",
        tcgPlayerDTO,
        null);
  }

  @Test
  void process_shouldReturnCorrectDTO_whenAllDataIsValid() {
    List<TcgPlayerPrice> tcgPlayerPrices = Arrays.asList(normalPrice, holoPrice);

    when(cardService.getById("swsh1-25")).thenReturn(card);
    when(tcgPlayerReportingMapper.toEntity(tcgPlayerDTO, card)).thenReturn(tcgPlayerReporting);
    when(tcgPlayerReportingService.getByUpdatedAtAndCard(tcgPlayerReporting)).thenReturn(tcgPlayerReporting);
    when(tcgPlayerPriceProcessor.processFromDTOs(pricesMap, tcgPlayerReporting)).thenReturn(tcgPlayerPrices);

    TcgPlayerPricesEntitiesProcessedDTO result = processor.process(cardPriceDTO);

    assertThat(result).isNotNull();
    assertThat(result.getTcgPlayerPrices()).isNotNull();
    assertThat(result.getTcgPlayerPrices()).hasSize(2);
    assertThat(result.getTcgPlayerPrices()).containsExactlyInAnyOrder(normalPrice, holoPrice);

    verify(cardService).getById("swsh1-25");
    verify(tcgPlayerReportingMapper).toEntity(tcgPlayerDTO, card);
    verify(tcgPlayerReportingService).getByUpdatedAtAndCard(tcgPlayerReporting);
    verify(tcgPlayerPriceProcessor).processFromDTOs(pricesMap, tcgPlayerReporting);
  }

  @Test
  void process_shouldReturnNull_whenTcgPlayerReportingMapperReturnsNull() {
    when(cardService.getById("swsh1-25")).thenReturn(card);
    when(tcgPlayerReportingMapper.toEntity(tcgPlayerDTO, card)).thenReturn(null);

    TcgPlayerPricesEntitiesProcessedDTO result = processor.process(cardPriceDTO);

    assertThat(result).isNull();

    verify(cardService).getById("swsh1-25");
    verify(tcgPlayerReportingMapper).toEntity(tcgPlayerDTO, card);
    verify(tcgPlayerReportingService, never()).getByUpdatedAtAndCard(any());
    verify(tcgPlayerPriceProcessor, never()).processFromDTOs(any(), any());
  }

  @Test
  void process_shouldReturnNull_whenTcgPlayerPriceProcessorReturnsEmptyList() {
    when(cardService.getById("swsh1-25")).thenReturn(card);
    when(tcgPlayerReportingMapper.toEntity(tcgPlayerDTO, card)).thenReturn(tcgPlayerReporting);
    when(tcgPlayerReportingService.getByUpdatedAtAndCard(tcgPlayerReporting)).thenReturn(tcgPlayerReporting);
    when(tcgPlayerPriceProcessor.processFromDTOs(pricesMap, tcgPlayerReporting)).thenReturn(Collections.emptyList());

    TcgPlayerPricesEntitiesProcessedDTO result = processor.process(cardPriceDTO);

    assertThat(result).isNull();

    verify(cardService).getById("swsh1-25");
    verify(tcgPlayerReportingMapper).toEntity(tcgPlayerDTO, card);
    verify(tcgPlayerReportingService).getByUpdatedAtAndCard(tcgPlayerReporting);
    verify(tcgPlayerPriceProcessor).processFromDTOs(pricesMap, tcgPlayerReporting);
  }

  @Test
  void process_shouldReturnNull_whenTcgPlayerPriceProcessorReturnsNull() {
    when(cardService.getById("swsh1-25")).thenReturn(card);
    when(tcgPlayerReportingMapper.toEntity(tcgPlayerDTO, card)).thenReturn(tcgPlayerReporting);
    when(tcgPlayerReportingService.getByUpdatedAtAndCard(tcgPlayerReporting)).thenReturn(tcgPlayerReporting);
    when(tcgPlayerPriceProcessor.processFromDTOs(pricesMap, tcgPlayerReporting)).thenReturn(null);

    TcgPlayerPricesEntitiesProcessedDTO result = processor.process(cardPriceDTO);

    assertThat(result).isNull();

    verify(cardService).getById("swsh1-25");
    verify(tcgPlayerReportingMapper).toEntity(tcgPlayerDTO, card);
    verify(tcgPlayerReportingService).getByUpdatedAtAndCard(tcgPlayerReporting);
    verify(tcgPlayerPriceProcessor).processFromDTOs(pricesMap, tcgPlayerReporting);
  }

  @Test
  void process_shouldReturnNull_whenCardHasNoTcgPlayerPrices() {
    CardPriceDTO cardWithoutTcgPlayer = CardPriceDTOMock.createMockCardPriceDTO(
        "swsh1-25",
        "Pikachu",
        null,
        null);

    when(cardService.getById("swsh1-25")).thenReturn(card);
    when(tcgPlayerReportingMapper.toEntity(null, card)).thenReturn(null);

    TcgPlayerPricesEntitiesProcessedDTO result = processor.process(cardWithoutTcgPlayer);

    assertThat(result).isNull();

    verify(cardService).getById("swsh1-25");
    verify(tcgPlayerReportingMapper).toEntity(null, card);
    verify(tcgPlayerReportingService, never()).getByUpdatedAtAndCard(any());
    verify(tcgPlayerPriceProcessor, never()).processFromDTOs(any(), any());
  }

  @Test
  void process_shouldHandleAndReturnNull_whenExceptionOccurs() {
    RuntimeException exception = new RuntimeException("Test exception");
    when(cardService.getById("swsh1-25")).thenThrow(exception);

    when(looterScrapingErrorHandler.formatErrorItem(anyString(), anyString()))
        .thenReturn("Formatted error message");
    doNothing().when(looterScrapingErrorHandler).handle(
        any(Exception.class),
        anyString(),
        anyString(),
        anyString());

    TcgPlayerPricesEntitiesProcessedDTO result = processor.process(cardPriceDTO);

    assertThat(result).isNull();

    verify(cardService).getById("swsh1-25");
    verify(looterScrapingErrorHandler).formatErrorItem(
        eq(Constantes.CARD_PRICE_DTO_ITEM),
        eq(cardPriceDTO.toString()));
    verify(looterScrapingErrorHandler).handle(
        eq(exception),
        eq(Constantes.CARD_DTO_TO_REPORTING_PRICE_ENTITY_PROCESSOR_CONTEXT),
        eq(Constantes.PROCESSOR_ACTION),
        anyString());
  }

  @Test
  void process_shouldHandleAndReturnNull_whenTcgPlayerReportingServiceThrowsException() {
    when(cardService.getById("swsh1-25")).thenReturn(card);
    when(tcgPlayerReportingMapper.toEntity(tcgPlayerDTO, card)).thenReturn(tcgPlayerReporting);
    when(tcgPlayerReportingService.getByUpdatedAtAndCard(tcgPlayerReporting))
        .thenThrow(new RuntimeException("Reporting not found"));

    when(looterScrapingErrorHandler.formatErrorItem(anyString(), anyString()))
        .thenReturn("Formatted error message");
    doNothing().when(looterScrapingErrorHandler).handle(
        any(Exception.class),
        anyString(),
        anyString(),
        anyString());

    TcgPlayerPricesEntitiesProcessedDTO result = processor.process(cardPriceDTO);

    assertThat(result).isNull();

    verify(cardService).getById("swsh1-25");
    verify(tcgPlayerReportingMapper).toEntity(tcgPlayerDTO, card);
    verify(tcgPlayerReportingService).getByUpdatedAtAndCard(tcgPlayerReporting);
    verify(tcgPlayerPriceProcessor, never()).processFromDTOs(any(), any());
    verify(looterScrapingErrorHandler).handle(
        any(Exception.class),
        eq(Constantes.CARD_DTO_TO_REPORTING_PRICE_ENTITY_PROCESSOR_CONTEXT),
        eq(Constantes.PROCESSOR_ACTION),
        anyString());
  }
}