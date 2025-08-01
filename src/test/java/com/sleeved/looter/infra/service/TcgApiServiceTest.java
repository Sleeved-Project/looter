package com.sleeved.looter.infra.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.sleeved.looter.common.util.Constantes;
import com.sleeved.looter.infra.service.TcgApiService.PageProcessor;
import com.sleeved.looter.mock.infra.TcgApiResponseMock;

@ExtendWith(MockitoExtension.class)
public class TcgApiServiceTest {

  @Mock
  private RestTemplate restTemplate;

  @Mock
  private LooterScrapingErrorHandler looterScrapingErrorHandler;

  @Mock
  private TcgApiUrlBuilder tcgApiUrlBuilder;

  @Mock
  private TcgApiRequestFactory tcgApiRequestFactory;

  @Mock
  private RestTemplateBuilder restTemplateBuilder;

  private TcgApiService tcgApiService;

  @BeforeEach
  void setUp() {
    when(restTemplateBuilder.build()).thenReturn(restTemplate);

    tcgApiService = new TcgApiService(
        restTemplateBuilder,
        looterScrapingErrorHandler,
        tcgApiUrlBuilder,
        tcgApiRequestFactory);

    tcgApiService = spy(tcgApiService);

    // Injection des valeurs de configuration via reflection
    ReflectionTestUtils.setField(tcgApiService, "self", tcgApiService);
    ReflectionTestUtils.setField(tcgApiService, "apiCardPaginateEndpoint",
        "/cards?page=%d&pageSize=%d&orderBy=set.releaseDate");
    ReflectionTestUtils.setField(tcgApiService, "apiCardPaginatePricesEndpoint",
        "/cards?page=%d&pageSize=%d&orderBy=set.releaseDate&select=id,name,tcgplayer,cardmarket");
    ReflectionTestUtils.setField(tcgApiService, "apiCardPageSize", 10);
    ReflectionTestUtils.setField(tcgApiService, "apiCardPage", 1);
    ReflectionTestUtils.setField(tcgApiService, "maxAttempts", "3");
    ReflectionTestUtils.setField(tcgApiService, "initalDelay", "1000");
    ReflectionTestUtils.setField(tcgApiService, "backoffMultiplier", "2.0");
    ReflectionTestUtils.setField(tcgApiService, "maxDelay", "10000");
  }

  @Test
  void fetchCardPage_shouldReturnJsonNodeData_whenApiCallIsSuccessful() {
    // Given
    String apiUrl = "https://api.tcgplayer.com/cards?page=1&pageSize=10";
    HttpEntity<String> httpEntity = new HttpEntity<>(null, null);
    JsonNode mockResponse = TcgApiResponseMock.createMockCardPage(5);
    ResponseEntity<JsonNode> responseEntity = ResponseEntity.ok(mockResponse);

    when(tcgApiUrlBuilder.buildPaginatedUrl(anyString(), anyInt(), anyInt())).thenReturn(apiUrl);
    when(tcgApiRequestFactory.createAuthorizedRequest()).thenReturn(httpEntity);
    when(restTemplate.exchange(
        eq(apiUrl),
        eq(HttpMethod.GET),
        eq(httpEntity),
        eq(JsonNode.class))).thenReturn(responseEntity);

    // When
    JsonNode result = tcgApiService.fetchCardPage(1);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isSameAs(mockResponse);
    verify(tcgApiUrlBuilder).buildPaginatedUrl(anyString(), eq(1), anyInt());
    verify(tcgApiRequestFactory).createAuthorizedRequest();
  }

  @Test
  void fetchCardPricePage_shouldReturnJsonNodeData_whenApiCallIsSuccessful() {
    // Given
    String apiUrl = "https://api.tcgplayer.com/card?page=1&pageSize=10";
    HttpEntity<String> httpEntity = new HttpEntity<>(null, null);
    JsonNode mockResponse = TcgApiResponseMock.createMockCardPage(5);
    ResponseEntity<JsonNode> responseEntity = ResponseEntity.ok(mockResponse);

    when(tcgApiUrlBuilder.buildPaginatedUrl(anyString(), anyInt(), anyInt())).thenReturn(apiUrl);
    when(tcgApiRequestFactory.createAuthorizedRequest()).thenReturn(httpEntity);
    when(restTemplate.exchange(
        eq(apiUrl),
        eq(HttpMethod.GET),
        eq(httpEntity),
        eq(JsonNode.class))).thenReturn(responseEntity);

    // When
    JsonNode result = tcgApiService.fetchCardPricePage(1);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isSameAs(mockResponse);
    verify(tcgApiUrlBuilder).buildPaginatedUrl(anyString(), eq(1), anyInt());
    verify(tcgApiRequestFactory).createAuthorizedRequest();
  }

  @Test
  void processAllCardsPageByPage_shouldProcessAllPagesSuccessfully_whenDataIsAvailable() {
    // Given
    JsonNode page1 = TcgApiResponseMock.createMockCardPageWithTotal(10, 25);
    JsonNode page2 = TcgApiResponseMock.createMockCardPageWithTotal(10, 25);
    JsonNode page3 = TcgApiResponseMock.createMockCardPageWithTotal(5, 25);

    doReturn(page1).when(tcgApiService).fetchCardPage(1);
    doReturn(page2).when(tcgApiService).fetchCardPage(2);
    doReturn(page3).when(tcgApiService).fetchCardPage(3);

    PageProcessor processor = mock(PageProcessor.class);

    // When
    tcgApiService.processAllCardsPageByPage(processor);

    // Then
    verify(processor, times(3)).processPage(any(), anyInt());
    verify(processor).processPage(any(), eq(1));
    verify(processor).processPage(any(), eq(2));
    verify(processor).processPage(any(), eq(3));
    verify(tcgApiService, times(3)).fetchCardPage(anyInt());
  }

  @Test
  void processAllCardsPageByPage_shouldStopProcessing_whenNoMoreDataIsAvailable() {
    // Given
    JsonNode page1 = TcgApiResponseMock.createMockCardPageWithTotal(10, 15);
    JsonNode emptyPage = TcgApiResponseMock.createMockEmptyCardPage();

    doReturn(page1).when(tcgApiService).fetchCardPage(1);
    doReturn(emptyPage).when(tcgApiService).fetchCardPage(2);

    PageProcessor processor = mock(PageProcessor.class);

    // When
    tcgApiService.processAllCardsPageByPage(processor);

    // Then
    verify(processor, times(1)).processPage(any(), eq(1));
    verify(processor, never()).processPage(any(), eq(2));
    verify(tcgApiService, times(2)).fetchCardPage(anyInt());
  }

  @Test
  void processAllCardsPageByPage_shouldHandleExceptionsGracefully_whenFetchingFails() {
    // Given
    doThrow(new RuntimeException("API Error")).when(tcgApiService).fetchCardPage(anyInt());
    PageProcessor processor = mock(PageProcessor.class);

    // When
    tcgApiService.processAllCardsPageByPage(processor);

    // Then
    verify(processor, never()).processPage(any(), anyInt());
    verify(looterScrapingErrorHandler).handle(
        any(RuntimeException.class),
        eq(Constantes.SERVICE_CONTEXT),
        eq(Constantes.FETCH_DATA_ACTION),
        eq(Constantes.TCGAPI_CARD_PAGINATE_ITEM));
  }

  @Test
  void processAllCardsPageByPage_shouldPassCorrectDataToProcessor_whenProcessingSinglePage() {
    // Given
    JsonNode page1 = TcgApiResponseMock.createMockCardPageWithTotal(3, 3);
    doReturn(page1).when(tcgApiService).fetchCardPage(1);

    List<JsonNode> capturedData = new ArrayList<>();
    AtomicInteger capturedPageNumber = new AtomicInteger();

    PageProcessor processor = (pageData, pageNumber) -> {
      capturedData.addAll(pageData);
      capturedPageNumber.set(pageNumber);
    };

    // When
    tcgApiService.processAllCardsPageByPage(processor);

    // Then
    assertThat(capturedData).hasSize(3);
    assertThat(capturedPageNumber.get()).isEqualTo(1);
  }

  @Test
  void processAllCardPricesPageByPage_shouldProcessAllPagesSuccessfully_whenDataIsAvailable() {
    // Given
    JsonNode page1 = TcgApiResponseMock.createMockCardPageWithTotal(10, 25);
    JsonNode page2 = TcgApiResponseMock.createMockCardPageWithTotal(10, 25);
    JsonNode page3 = TcgApiResponseMock.createMockCardPageWithTotal(5, 25);

    doReturn(page1).when(tcgApiService).fetchCardPricePage(1);
    doReturn(page2).when(tcgApiService).fetchCardPricePage(2);
    doReturn(page3).when(tcgApiService).fetchCardPricePage(3);

    PageProcessor processor = mock(PageProcessor.class);

    // When
    tcgApiService.processAllCardPricesPageByPage(processor);

    // Then
    verify(processor, times(3)).processPage(any(), anyInt());
    verify(processor).processPage(any(), eq(1));
    verify(processor).processPage(any(), eq(2));
    verify(processor).processPage(any(), eq(3));
    verify(tcgApiService, times(3)).fetchCardPricePage(anyInt());
  }

  @Test
  void processAllCardPricesPageByPage_shouldStopProcessing_whenNoMoreDataIsAvailable() {
    // Given
    JsonNode page1 = TcgApiResponseMock.createMockCardPageWithTotal(10, 15);
    JsonNode emptyPage = TcgApiResponseMock.createMockEmptyCardPage();

    doReturn(page1).when(tcgApiService).fetchCardPricePage(1);
    doReturn(emptyPage).when(tcgApiService).fetchCardPricePage(2);

    PageProcessor processor = mock(PageProcessor.class);

    // When
    tcgApiService.processAllCardPricesPageByPage(processor);

    // Then
    verify(processor, times(1)).processPage(any(), eq(1));
    verify(processor, never()).processPage(any(), eq(2));
    verify(tcgApiService, times(2)).fetchCardPricePage(anyInt());
  }

  @Test
  void processAllCardPricesPageByPage_shouldHandleExceptionsGracefully_whenFetchingFails() {
    // Given
    doThrow(new RuntimeException("API Error")).when(tcgApiService).fetchCardPricePage(anyInt());
    PageProcessor processor = mock(PageProcessor.class);

    // When
    tcgApiService.processAllCardPricesPageByPage(processor);

    // Then
    verify(processor, never()).processPage(any(), anyInt());
    verify(looterScrapingErrorHandler).handle(
        any(RuntimeException.class),
        eq(Constantes.SERVICE_CONTEXT),
        eq(Constantes.FETCH_DATA_ACTION),
        eq(Constantes.TCGAPI_CARD_PAGINATE_ITEM));
  }

  @Test
  void processAllCardPricesPageByPage_shouldPassCorrectDataToProcessor_whenProcessingSinglePage() {
    // Given
    JsonNode page1 = TcgApiResponseMock.createMockCardPageWithTotal(3, 3);
    doReturn(page1).when(tcgApiService).fetchCardPricePage(1);

    List<JsonNode> capturedData = new ArrayList<>();
    AtomicInteger capturedPageNumber = new AtomicInteger();

    PageProcessor processor = (pageData, pageNumber) -> {
      capturedData.addAll(pageData);
      capturedPageNumber.set(pageNumber);
    };

    // When
    tcgApiService.processAllCardPricesPageByPage(processor);

    // Then
    assertThat(capturedData).hasSize(3);
    assertThat(capturedPageNumber.get()).isEqualTo(1);
  }

  @Test
  void recoverFromFetchCardPage_shouldThrowRuntimeException_whenAllRetriesFail() {
    // Given
    RuntimeException originalException = new RuntimeException("API Error");
    int page = 1;

    // When & Then
    assertThatThrownBy(() -> tcgApiService.recoverFromFetchCardPage(originalException, page))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("Unable to fetch card page 1 after retries")
        .hasCause(originalException);
  }

  @Test
  void recoverFromFetchCardPricePage_shouldThrowRuntimeException_whenAllRetriesFail() {
    // Given
    RuntimeException originalException = new RuntimeException("API Error");
    int page = 1;

    // When & Then
    assertThatThrownBy(() -> tcgApiService.recoverFromFetchCardPricePage(originalException, page))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("Unable to fetch card price page 1 after retries")
        .hasCause(originalException);
  }

  @Test
  void processAllCardsPageByPage_shouldThrowRuntimeException_whenResponseIsNull() {
    // Given
    doReturn(null).when(tcgApiService).fetchCardPage(1);
    PageProcessor processor = mock(PageProcessor.class);

    // When
    tcgApiService.processAllCardsPageByPage(processor);

    // Then
    verify(processor, never()).processPage(any(), anyInt());
    verify(looterScrapingErrorHandler).handle(
        any(RuntimeException.class),
        eq(Constantes.SERVICE_CONTEXT),
        eq(Constantes.FETCH_DATA_ACTION),
        eq(Constantes.TCGAPI_CARD_PAGINATE_ITEM));
  }

  @Test
  void processAllCardPricesPageByPage_shouldThrowRuntimeException_whenResponseIsNull() {
    // Given
    doReturn(null).when(tcgApiService).fetchCardPricePage(1);
    PageProcessor processor = mock(PageProcessor.class);

    // When
    tcgApiService.processAllCardPricesPageByPage(processor);

    // Then
    verify(processor, never()).processPage(any(), anyInt());
    verify(looterScrapingErrorHandler).handle(
        any(RuntimeException.class),
        eq(Constantes.SERVICE_CONTEXT),
        eq(Constantes.FETCH_DATA_ACTION),
        eq(Constantes.TCGAPI_CARD_PAGINATE_ITEM));
  }
}