package com.sleeved.looter.infra.service;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sleeved.looter.common.util.Constantes;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class IrisApiService {

  private final LooterScrapingErrorHandler looterScrapingErrorHandler;
  private final RestTemplate restTemplate;
  private final IrisApiUrlBuilder irisApiUrlBuilder;
  private final ObjectMapper objectMapper;

  public IrisApiService(RestTemplateBuilder builder,
      LooterScrapingErrorHandler looterScrapingErrorHandler, 
      IrisApiUrlBuilder irisApiUrlBuilder,
      ObjectMapper objectMapper) {
    this.restTemplate = builder.build();
    this.looterScrapingErrorHandler = looterScrapingErrorHandler;
    this.irisApiUrlBuilder = irisApiUrlBuilder;
    this.objectMapper = objectMapper;
  }

  public String fetchHashImage(String imageUrl) {
    try {
        if (imageUrl == null) {
            String formatedItem = looterScrapingErrorHandler.formatErrorItem(
                Constantes.HASH_IMAGE_ITEM, imageUrl);
            looterScrapingErrorHandler.handle(
                new NullPointerException("Image URL is null"),
                Constantes.HASH_IMAGE_FETCH_CONTEXT, 
                Constantes.FETCH_DATA_ACTION,
                formatedItem);
            return null;
        }

        String apiUrl = irisApiUrlBuilder.buildUrl();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("url", imageUrl);
        
        HttpEntity<String> requestEntity = new HttpEntity<>(
            objectMapper.writeValueAsString(requestBody), headers);
        
        ResponseEntity<JsonNode> response = restTemplate.exchange(
            apiUrl, 
            HttpMethod.POST, 
            requestEntity, 
            JsonNode.class);
        
        JsonNode body = response.getBody();
        if (body != null && body.has("hash")) {
            String hash = body.get("hash").asText();
            return hash;
        } else {
            String formatedItem = looterScrapingErrorHandler.formatErrorItem(
                Constantes.HASH_IMAGE_ITEM, imageUrl);
            looterScrapingErrorHandler.handle(
                new RuntimeException("Hash not found in response"),
                Constantes.HASH_IMAGE_FETCH_CONTEXT, 
                Constantes.FETCH_DATA_ACTION,
                formatedItem);
            return null;
        }
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