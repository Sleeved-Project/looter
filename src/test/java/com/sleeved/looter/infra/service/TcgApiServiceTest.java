package com.sleeved.looter.infra.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

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

    ReflectionTestUtils.setField(tcgApiService, "apiCardPaginateEndpoint",
        "/cards?page=%d&pageSize=%d&orderBy=set.releaseDate");
    ReflectionTestUtils.setField(tcgApiService, "apiCardPaginatePricesEndpoint",
        "/cards?page=%d&pageSize=%d&orderBy=set.releaseDate&select=id,name,tcgplayer,cardmarket");
    ReflectionTestUtils.setField(tcgApiService, "apiCardPageSize", 10);
    ReflectionTestUtils.setField(tcgApiService, "apiCardPage", 1);
  }

  @Test
  void fetchCardPage_shouldReturnJsonNodeData() {
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

    JsonNode result = tcgApiService.fetchCardPage(1);

    assertThat(result).isNotNull();
    assertThat(result).isSameAs(mockResponse);
    verify(tcgApiUrlBuilder).buildPaginatedUrl(anyString(), eq(1), anyInt());
    verify(tcgApiRequestFactory).createAuthorizedRequest();
  }

  @Test
  void fetchAllCards_shouldReturnListOfCards() {
    JsonNode page1 = TcgApiResponseMock.createMockCardPage(10);
    JsonNode page2 = TcgApiResponseMock.createMockCardPage(10);

    doReturn(page1).when(tcgApiService).fetchCardPage(1);
    doReturn(page2).when(tcgApiService).fetchCardPage(2);

    List<JsonNode> result = tcgApiService.fetchAllCards();

    assertThat(result).hasSize(20);
    verify(tcgApiService, times(2)).fetchCardPage(anyInt());
  }

  @Test
  void fetchAllCards_shouldHandleExceptions() {
    doReturn(null).when(tcgApiService).fetchCardPage(anyInt());

    List<JsonNode> result = tcgApiService.fetchAllCards();

    assertThat(result).isEmpty();
    verify(looterScrapingErrorHandler).handle(
        any(Exception.class),
        eq(Constantes.SERVICE_CONTEXT),
        eq(Constantes.FETCH_DATA_ACTION),
        eq(Constantes.TCGAPI_CARD_PAGINATE_ITEM));
  }

  @Test
  void fetchCardPricePage_shouldReturnJsonNodeData() {
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

    JsonNode result = tcgApiService.fetchCardPricePage(1);

    assertThat(result).isNotNull();
    assertThat(result).isSameAs(mockResponse);
    verify(tcgApiUrlBuilder).buildPaginatedUrl(anyString(), eq(1), anyInt());
    verify(tcgApiRequestFactory).createAuthorizedRequest();
  }

  @Test
  void fetchAllCardPrices_shouldReturnListOfCardPrices() {
    JsonNode page1 = TcgApiResponseMock.createMockCardPage(10);
    JsonNode page2 = TcgApiResponseMock.createMockCardPage(10);

    doReturn(page1).when(tcgApiService).fetchCardPricePage(1);
    doReturn(page2).when(tcgApiService).fetchCardPricePage(2);

    List<JsonNode> result = tcgApiService.fetchAllCardPrices();

    assertThat(result).hasSize(20);
    verify(tcgApiService, times(2)).fetchCardPricePage(anyInt());
  }

  @Test
  void fetchAllCardPrices_shouldHandleExceptions() {
    doReturn(null).when(tcgApiService).fetchCardPricePage(anyInt());

    List<JsonNode> result = tcgApiService.fetchAllCardPrices();

    assertThat(result).isEmpty();
    verify(looterScrapingErrorHandler).handle(
        any(Exception.class),
        eq(Constantes.SERVICE_CONTEXT),
        eq(Constantes.FETCH_DATA_ACTION),
        eq(Constantes.TCGAPI_CARD_PAGINATE_ITEM));
  }
}
