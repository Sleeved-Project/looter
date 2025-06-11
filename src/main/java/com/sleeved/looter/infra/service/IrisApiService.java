package com.sleeved.looter.infra.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.sleeved.looter.common.util.Constantes;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class IrisApiService {

  private final LooterScrapingErrorHandler looterScrapingErrorHandler;
  private final RestTemplate restTemplate;
  private final IrisApiUrlBuilder irisApiUrlBuilder;
  private final IrisApiRequestFactory requestFactory;

  @Value("${iris.api.url.endpoint.hash}")
  private String endpoint;

  public IrisApiService(RestTemplateBuilder builder,
      LooterScrapingErrorHandler looterScrapingErrorHandler, 
      IrisApiUrlBuilder irisApiUrlBuilder,
    IrisApiRequestFactory requestFactory
  ) {
    this.restTemplate = builder.build();
    this.looterScrapingErrorHandler = looterScrapingErrorHandler;
    this.irisApiUrlBuilder = irisApiUrlBuilder;
    this.requestFactory = requestFactory;
  }

  public JsonNode fetchHashImage(String imageUrl) {
    try {
        String apiUrl = irisApiUrlBuilder.buildUrl(endpoint);
        HttpEntity<String> requestEntity = requestFactory.createHashImageRequest(imageUrl);
        
        ResponseEntity<JsonNode> response = restTemplate.exchange(
            apiUrl, 
            HttpMethod.POST, 
            requestEntity, 
            JsonNode.class);
        
        JsonNode body = response.getBody();
        if (body == null || !body.has("hash")) {
          throw new RuntimeException("Invalid response format or missing 'hash' field");
        }
        return body;
    } catch (HttpClientErrorException e) {
      log.error("Bad request error while fetching hash image: {}", e.getMessage());
      return null;
    } catch (Exception e) {
      String formatedItem = looterScrapingErrorHandler.formatErrorItem(
          Constantes.HASH_IMAGE_ITEM, imageUrl);
      looterScrapingErrorHandler.handle(e,
          Constantes.HASH_IMAGE_FETCH_CONTEXT, 
          Constantes.FETCH_DATA_ACTION, 
          formatedItem);
      return null;
    }
  }
}