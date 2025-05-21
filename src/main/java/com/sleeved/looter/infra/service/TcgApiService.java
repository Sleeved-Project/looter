package com.sleeved.looter.infra.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
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

  @Value("${tcgplayer.api.endpoints.cards.paginate}")
  private String apiCardPaginateEndpoint;
  @Value("${tcgplayer.api.endpoints.cards.pagesize}")
  private int apiCardPageSize;
  @Value("${tcgplayer.api.endpoints.cards.page}")
  private int apiCardPage;

  public TcgApiService(RestTemplateBuilder builder, LooterScrapingErrorHandler looterScrapingErrorHandler,
      TcgApiUrlBuilder tcgApiUrlBuilder, TcgApiRequestFactory tcgApiRequestFactory) {
    this.restTemplate = builder.build();
    this.looterScrapingErrorHandler = looterScrapingErrorHandler;
    this.tcgApiUrlBuilder = tcgApiUrlBuilder;
    this.tcgApiRequestFactory = tcgApiRequestFactory;
  }

  public List<JsonNode> fetchAllCards() {
    int page = apiCardPage;
    List<JsonNode> allCards = new ArrayList<>();

    while (true) {
      try {

        JsonNode root = fetchCardPage(page);
        if (root == null || !root.has("data") || !root.get("data").isArray()) {
          throw new RuntimeException("No data found or invalid response");
        }

        JsonNode cards = root.get("data");
        if (!cards.isArray() || cards.size() == 0) {
          log.info("No more cards to fetch or invalid response format");
          break;
        }

        for (JsonNode card : cards) {
          allCards.add(card);
        }

        // ! Real condition for production
        // int total = root.path("totalCount").asInt();
        // if (page * apiCardPageSize >= total) {
        // log.info("All cards fetched");
        // break;
        // }

        // ! Test condition for testing
        if (page * apiCardPageSize >= apiCardPageSize * 20) {
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

    return allCards;
  }

  protected JsonNode fetchCardPage(int page) {
    String apiUrl = tcgApiUrlBuilder.buildPaginatedUrl(apiCardPaginateEndpoint, page, apiCardPageSize);
    log.info("Fetching cards from TCG API: {}", apiUrl);
    ResponseEntity<JsonNode> response = restTemplate.exchange(apiUrl, HttpMethod.GET,
        tcgApiRequestFactory.createAuthorizedRequest(),
        JsonNode.class);
    log.info("Response status code: {}", response.getStatusCode());
    return response.getBody();
  }

}
