package com.sleeved.looter.infra.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
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
  private final RestTemplate restTemplate;

  @Value("${tcgplayer.api.url.protocole}")
  private String apiProtocole;

  @Value("${tcgplayer.api.url.domain}")
  private String apiDomain;

  @Value("${tcgplayer.api.url.base}")
  private String apiBaseUrl;

  @Value("${tcgplayer.api.endpoints.cards.paginate}")
  private String apiCardPaginateEndpoint;
  @Value("${tcgplayer.api.endpoints.cards.pagesize}")
  private int apiCardPageSize;
  @Value("${tcgplayer.api.endpoints.cards.page}")
  private int apiCardPage;

  @Value("${tcgplayer.api.key}")
  private String apiKey;

  public TcgApiService(RestTemplateBuilder builder) {
    this.restTemplate = builder.build();
  }

  public List<JsonNode> fetchAllCards() {
    int page = apiCardPage;
    List<JsonNode> allCards = new ArrayList<>();

    while (true) {
      HttpHeaders headers = new HttpHeaders();
      headers.set("Authorization", "Bearer " + apiKey);

      String endpointPaginated = updateEndpointPaginationUrl(apiCardPaginateEndpoint, apiCardPageSize, page);
      String apiUrl = getApiUrl(apiBaseUrl, endpointPaginated);
      log.info("API URL: {}", apiUrl);
      // 2. Envelopper les headers
      HttpEntity<String> entity = new HttpEntity<>(headers);

      // 3. Requête avec échange
      ResponseEntity<JsonNode> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, JsonNode.class);

      JsonNode root = response.getBody();
      log.info("API Response Done");
      if (root == null) {
        throw new RuntimeException("Failed to fetch data from TCGPlayer API");
      }

      JsonNode cards = root.path("data");

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
      if (page * apiCardPageSize >= apiCardPageSize * 2) {
        log.info("All cards fetched");
        break;
      }

      page++;
    }

    return allCards;
  }

  public String updateEndpointPaginationUrl(String endpoint, Integer pageSize, Integer page) {
    return String.format(endpoint, page, pageSize);
  }

  public String getApiUrl(String tcgPlayerBaseUrl, String tcgPlayerEndpoint) {
    String tcgPlayerUrl = String.format(Constantes.API_URL_FORMAT, apiProtocole, apiDomain);
    return String.format(Constantes.TCG_API_URL_BASE_FORMAT, tcgPlayerUrl, tcgPlayerBaseUrl,
        tcgPlayerEndpoint);
  }

}
