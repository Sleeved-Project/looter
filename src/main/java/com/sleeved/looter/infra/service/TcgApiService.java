package com.sleeved.looter.infra.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.sleeved.looter.common.util.Constantes;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TcgApiService {

  private final LooterScrapingErrorHandler looterScrapingErrorHandler;
  private final RestTemplate restTemplate;
  private final TcgApiUrlBuilder tcgApiUrlBuilder;
  private final TcgApiRequestFactory tcgApiRequestFactory;

  @Autowired
  private TcgApiService self;

  @Value("${tcgplayer.api.endpoints.cards.paginate}")
  private String apiCardPaginateEndpoint;
  @Value("${tcgplayer.api.endpoints.cards.paginate-prices}")
  private String apiCardPaginatePricesEndpoint;
  @Value("${tcgplayer.api.endpoints.cards.pagesize}")
  private int apiCardPageSize;
  @Value("${tcgplayer.api.endpoints.cards.page}")
  private int apiCardPage;

  private final String maxAttempts = "#{${tcgplayer.api.retry.max-attempts}}";
  private final String initalDelay = "#{${tcgplayer.api.retry.initial-delay}}";
  private final String backoffMultiplier = "#{${tcgplayer.api.retry.backoff-multiplier}}";
  private final String maxDelay = "#{${tcgplayer.api.retry.max-delay}}";

  public TcgApiService(RestTemplateBuilder builder, LooterScrapingErrorHandler looterScrapingErrorHandler,
      TcgApiUrlBuilder tcgApiUrlBuilder, TcgApiRequestFactory tcgApiRequestFactory) {
    this.restTemplate = builder.build();
    this.looterScrapingErrorHandler = looterScrapingErrorHandler;
    this.tcgApiUrlBuilder = tcgApiUrlBuilder;
    this.tcgApiRequestFactory = tcgApiRequestFactory;
  }

  public void processAllCardsPageByPage(PageProcessor processor) {
    int page = apiCardPage;

    while (true) {
      try {

        JsonNode responseBody = self.fetchCardPage(page);
        if (responseBody == null || !responseBody.has("data") || !responseBody.get("data").isArray()) {
          throw new RuntimeException("No data found or invalid response");
        }

        JsonNode cards = responseBody.get("data");
        if (!cards.isArray() || cards.size() == 0) {
          log.info("No more cards to fetch or invalid response format");
          break;
        }

        List<JsonNode> cardsPage = new ArrayList<>();
        for (JsonNode card : cards) {
          cardsPage.add(card);
        }

        processor.processPage(cardsPage, page);

        int total = responseBody.path("totalCount").asInt();
        if (page * apiCardPageSize >= total) {
          log.info("All cards fetched");
          break;
        }

        page++;
      } catch (Exception e) {
        looterScrapingErrorHandler.handle(e, Constantes.SERVICE_CONTEXT, Constantes.FETCH_DATA_ACTION,
            Constantes.TCGAPI_CARD_PAGINATE_ITEM);
        break;
      }
    }
  }

  public void processAllCardPricesPageByPage(PageProcessor processor) {
    int page = apiCardPage;

    while (true) {
      try {

        JsonNode responseBody = self.fetchCardPricePage(page);
        if (responseBody == null || !responseBody.has("data") || !responseBody.get("data").isArray()) {
          throw new RuntimeException("No data found or invalid response");
        }

        JsonNode cardPrices = responseBody.get("data");
        if (!cardPrices.isArray() || cardPrices.size() == 0) {
          log.info("No more cards to fetch or invalid response format");
          break;
        }

        List<JsonNode> cardPricesPage = new ArrayList<>();
        for (JsonNode cardPrice : cardPrices) {
          cardPricesPage.add(cardPrice);
        }

        processor.processPage(cardPricesPage, page);

        int total = responseBody.path("totalCount").asInt();
        if (page * apiCardPageSize >= total) {
          log.info("All cards fetched");
          break;
        }

        page++;
      } catch (Exception e) {
        looterScrapingErrorHandler.handle(e, Constantes.SERVICE_CONTEXT, Constantes.FETCH_DATA_ACTION,
            Constantes.TCGAPI_CARD_PAGINATE_ITEM);
        break;
      }
    }
  }

  @Retryable(retryFor = { HttpServerErrorException.class, ResourceAccessException.class,
      HttpClientErrorException.class }, noRetryFor = { HttpClientErrorException.BadRequest.class,
          HttpClientErrorException.Unauthorized.class,
          HttpClientErrorException.Forbidden.class }, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2.0, maxDelay = 10000))
  public JsonNode fetchCardPage(int page) {
    String apiUrl = tcgApiUrlBuilder.buildPaginatedUrl(apiCardPaginateEndpoint, page, apiCardPageSize);
    log.info("Fetching cards from TCG API: {}", apiUrl);
    ResponseEntity<JsonNode> response = restTemplate.exchange(apiUrl, HttpMethod.GET,
        tcgApiRequestFactory.createAuthorizedRequest(),
        JsonNode.class);
    log.info("Response status code: {}", response.getStatusCode());
    return response.getBody();
  }

  @Retryable(retryFor = { HttpServerErrorException.class, ResourceAccessException.class,
      HttpClientErrorException.class }, noRetryFor = { HttpClientErrorException.BadRequest.class,
          HttpClientErrorException.Unauthorized.class,
          HttpClientErrorException.Forbidden.class }, maxAttemptsExpression = maxAttempts, backoff = @Backoff(delayExpression = initalDelay, multiplierExpression = backoffMultiplier, maxDelayExpression = maxDelay))
  public JsonNode fetchCardPricePage(int page) {
    String apiUrl = tcgApiUrlBuilder.buildPaginatedUrl(apiCardPaginatePricesEndpoint, page, apiCardPageSize);
    log.info("Fetching card prices from TCG API: {}", apiUrl);
    ResponseEntity<JsonNode> response = restTemplate.exchange(apiUrl, HttpMethod.GET,
        tcgApiRequestFactory.createAuthorizedRequest(),
        JsonNode.class);
    log.info("Response status code: {}", response.getStatusCode());
    return response.getBody();
  }

  @Recover
  public JsonNode recoverFromFetchCardPage(Exception e, int page) {
    log.error("Failed to fetch card page {} after all retry attempts. Error: {}", page, e.getMessage());
    throw new RuntimeException("Unable to fetch card page " + page + " after retries", e);
  }

  @Recover
  public JsonNode recoverFromFetchCardPricePage(Exception e, int page) {
    log.error("Failed to fetch card price page {} after all retry attempts. Error: {}", page, e.getMessage());
    throw new RuntimeException("Unable to fetch card price page " + page + " after retries", e);
  }

  @FunctionalInterface
  public interface PageProcessor {
    void processPage(List<JsonNode> pageData, int pageNumber);
  }

}
