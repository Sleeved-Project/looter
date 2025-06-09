package com.sleeved.looter.infra.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sleeved.looter.common.util.Constantes;

@ExtendWith(MockitoExtension.class)
public class IrisApiServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private LooterScrapingErrorHandler looterScrapingErrorHandler;

    @Mock
    private IrisApiUrlBuilder irisApiUrlBuilder;

    @Mock
    private RestTemplateBuilder restTemplateBuilder;
    
    @Mock
    private ObjectMapper mockObjectMapper;

    private IrisApiService irisApiService;

    private static final String TEST_IMAGE_URL = "https://images.pokemontcg.io/base1/1_hires.png";
    private static final String TEST_API_URL = "http://localhost:8083/api/v1/images/hash/url";
    private static final String TEST_HASH = "ff00ff00ff00ff00a1b2c3d4e5f67890";
    private static final String FORMATTED_ERROR = "Formatted error message";

    @BeforeEach
    void setUp() {
        when(restTemplateBuilder.build()).thenReturn(restTemplate);
        
        lenient().when(irisApiUrlBuilder.buildUrl()).thenReturn(TEST_API_URL);
        
        irisApiService = new IrisApiService(
                restTemplateBuilder,
                looterScrapingErrorHandler,
                irisApiUrlBuilder,
                mockObjectMapper);
        
        irisApiService = spy(irisApiService);
        
        lenient().when(looterScrapingErrorHandler.formatErrorItem(
                eq(Constantes.HASH_IMAGE_ITEM), anyString()))
                .thenReturn(FORMATTED_ERROR);
    }

    @Test
    void fetchHashImage_shouldReturnHash_whenApiCallIsSuccessful() throws Exception {
        ObjectNode responseBody = new ObjectMapper().createObjectNode();
        responseBody.put("hash", TEST_HASH);
        ResponseEntity<JsonNode> mockResponse = ResponseEntity.ok(responseBody);
        
        ObjectNode requestBody = new ObjectMapper().createObjectNode();
        when(mockObjectMapper.createObjectNode()).thenReturn(requestBody);
        when(mockObjectMapper.writeValueAsString(any(ObjectNode.class))).thenReturn("{\"url\":\"" + TEST_IMAGE_URL + "\"}");

        when(restTemplate.exchange(
                eq(TEST_API_URL),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(JsonNode.class)))
                .thenReturn(mockResponse);

        String result = irisApiService.fetchHashImage(TEST_IMAGE_URL);

        assertThat(result)
            .as("Le service devrait retourner le hash extrait de la réponse")
            .isEqualTo(TEST_HASH);
        
        verify(irisApiUrlBuilder, times(1)).buildUrl();
        verify(mockObjectMapper, times(1)).createObjectNode();
        verify(mockObjectMapper, times(1)).writeValueAsString(any(ObjectNode.class));
    }

    @Test
    void fetchHashImage_shouldReturnNull_whenHashIsMissingInResponse() throws Exception {
        ObjectNode responseBody = new ObjectMapper().createObjectNode();
        responseBody.put("status", "success"); // Pas de hash dans la réponse
        ResponseEntity<JsonNode> mockResponse = ResponseEntity.ok(responseBody);
        
        ObjectNode requestBody = new ObjectMapper().createObjectNode();
        when(mockObjectMapper.createObjectNode()).thenReturn(requestBody);
        when(mockObjectMapper.writeValueAsString(any(ObjectNode.class))).thenReturn("{\"url\":\"" + TEST_IMAGE_URL + "\"}");

        when(restTemplate.exchange(
                eq(TEST_API_URL),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(JsonNode.class)))
                .thenReturn(mockResponse);

        String result = irisApiService.fetchHashImage(TEST_IMAGE_URL);

        assertThat(result)
            .as("Le service devrait retourner null quand le hash est absent de la réponse")
            .isNull();
        
        verify(looterScrapingErrorHandler).handle(
                any(RuntimeException.class),
                eq(Constantes.HASH_IMAGE_FETCH_CONTEXT),
                eq(Constantes.FETCH_DATA_ACTION),
                eq(FORMATTED_ERROR));
    }

    @Test
    void fetchHashImage_shouldReturnNull_whenApiReturnsNullBody() throws Exception {
        ResponseEntity<JsonNode> mockResponse = ResponseEntity.ok(null);

        ObjectNode requestBody = new ObjectMapper().createObjectNode();
        when(mockObjectMapper.createObjectNode()).thenReturn(requestBody);
        when(mockObjectMapper.writeValueAsString(any(ObjectNode.class))).thenReturn("{\"url\":\"" + TEST_IMAGE_URL + "\"}");

        when(restTemplate.exchange(
                eq(TEST_API_URL),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(JsonNode.class)))
                .thenReturn(mockResponse);

        String result = irisApiService.fetchHashImage(TEST_IMAGE_URL);

        assertThat(result)
            .as("Le service devrait retourner null quand la réponse est null")
            .isNull();
        
        verify(looterScrapingErrorHandler).handle(
                any(RuntimeException.class),
                eq(Constantes.HASH_IMAGE_FETCH_CONTEXT),
                eq(Constantes.FETCH_DATA_ACTION),
                eq(FORMATTED_ERROR));
    }

    @Test
    void fetchHashImage_shouldHandleHttpClientError() {
        HttpClientErrorException exception = new HttpClientErrorException(HttpStatus.NOT_FOUND);
        
        ObjectNode requestBody = new ObjectMapper().createObjectNode();
        when(mockObjectMapper.createObjectNode()).thenReturn(requestBody);
        
        try {
            when(mockObjectMapper.writeValueAsString(any(ObjectNode.class))).thenReturn("{\"url\":\"" + TEST_IMAGE_URL + "\"}");
        } catch (Exception e) {
            looterScrapingErrorHandler.handle(
                    e,
                    Constantes.HASH_IMAGE_FETCH_CONTEXT,
                    Constantes.FETCH_DATA_ACTION,
                    FORMATTED_ERROR);
        }

        when(restTemplate.exchange(
                eq(TEST_API_URL),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(JsonNode.class)))
                .thenThrow(exception);

        String result = irisApiService.fetchHashImage(TEST_IMAGE_URL);

        assertThat(result)
            .as("Le service devrait retourner null en cas d'erreur HTTP")
            .isNull();
        
        verify(looterScrapingErrorHandler).handle(
                eq(exception),
                eq(Constantes.HASH_IMAGE_FETCH_CONTEXT),
                eq(Constantes.FETCH_DATA_ACTION),
                eq(FORMATTED_ERROR));
    }

    @Test
    void fetchHashImage_shouldHandleJsonProcessingException() throws Exception {
        RuntimeException exception = new RuntimeException("JSON processing error");
        
        ObjectNode requestBody = new ObjectMapper().createObjectNode();
        when(mockObjectMapper.createObjectNode()).thenReturn(requestBody);
        when(mockObjectMapper.writeValueAsString(any(ObjectNode.class))).thenThrow(exception);

        String result = irisApiService.fetchHashImage(TEST_IMAGE_URL);

        assertThat(result)
            .as("Le service devrait retourner null en cas d'erreur de traitement JSON")
            .isNull();
        
        verify(looterScrapingErrorHandler).handle(
                eq(exception),
                eq(Constantes.HASH_IMAGE_FETCH_CONTEXT),
                eq(Constantes.FETCH_DATA_ACTION),
                eq(FORMATTED_ERROR));
    }

    @Test
    void fetchHashImage_shouldHandleNullImageUrl() {
        when(looterScrapingErrorHandler.formatErrorItem(
                eq(Constantes.HASH_IMAGE_ITEM),
                eq(null)))
                .thenReturn(FORMATTED_ERROR);

        String result = irisApiService.fetchHashImage(null);

        assertThat(result)
            .as("Le service devrait retourner null quand l'URL est null")
            .isNull();
        
        verify(looterScrapingErrorHandler).handle(
                any(NullPointerException.class),
                eq(Constantes.HASH_IMAGE_FETCH_CONTEXT),
                eq(Constantes.FETCH_DATA_ACTION),
                eq(FORMATTED_ERROR));
    }
}