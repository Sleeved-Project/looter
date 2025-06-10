package com.sleeved.looter.infra.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sleeved.looter.common.util.Constantes;

@ExtendWith(MockitoExtension.class)
class IrisApiServiceTest {

    @Mock
    private RestTemplate restTemplate;
    
    @Mock
    private RestTemplateBuilder restTemplateBuilder;
    
    @Mock
    private LooterScrapingErrorHandler looterScrapingErrorHandler;
    
    @Mock
    private IrisApiUrlBuilder irisApiUrlBuilder;
    
    @Mock
    private IrisApiRequestFactory requestFactory;
    
    @Mock
    private HttpEntity<String> requestEntity;
    
    private IrisApiService irisApiService;
    private ObjectMapper objectMapper = new ObjectMapper();
    
    @BeforeEach
    void setUp() {
        when(restTemplateBuilder.build()).thenReturn(restTemplate);
        
        irisApiService = new IrisApiService(
            restTemplateBuilder,
            looterScrapingErrorHandler,
            irisApiUrlBuilder,
            requestFactory
        );
        
        ReflectionTestUtils.setField(irisApiService, "endpoint", "/hash");
    }

    @Test
    void fetchHashImage_ShouldReturnValidJsonNode_WhenApiCallSucceeds() throws Exception {
        String imageUrl = "https://example.com/image.jpg";
        String apiUrl = "https://iris-api.com/hash";
        String expectedHash = "abc123def456";
        
        JsonNode expectedResponse = objectMapper.createObjectNode()
            .put("hash", expectedHash);
        
        when(irisApiUrlBuilder.buildUrl("/hash")).thenReturn(apiUrl);
        when(requestFactory.createHashImageRequest(imageUrl)).thenReturn(requestEntity);
        when(restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, JsonNode.class))
            .thenReturn(ResponseEntity.ok(expectedResponse));
        
        JsonNode result = irisApiService.fetchHashImage(imageUrl);
        
        assertNotNull(result);
        assertTrue(result.has("hash"));
        assertEquals(expectedHash, result.get("hash").asText());
        
        verify(irisApiUrlBuilder).buildUrl("/hash");
        verify(requestFactory).createHashImageRequest(imageUrl);
        verify(restTemplate).exchange(apiUrl, HttpMethod.POST, requestEntity, JsonNode.class);
        verifyNoInteractions(looterScrapingErrorHandler);
    }

    @Test
    void fetchHashImage_ShouldThrowException_WhenResponseBodyIsNull() throws Exception {
        String imageUrl = "https://example.com/image.jpg";
        String apiUrl = "https://iris-api.com/hash";
        String formattedItem = "formatted-error-item";
        
        when(irisApiUrlBuilder.buildUrl("/hash")).thenReturn(apiUrl);
        when(requestFactory.createHashImageRequest(imageUrl)).thenReturn(requestEntity);
        when(restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, JsonNode.class))
            .thenReturn(ResponseEntity.ok(null));
        when(looterScrapingErrorHandler.formatErrorItem(Constantes.HASH_IMAGE_ITEM, imageUrl))
            .thenReturn(formattedItem);
        
        JsonNode result = irisApiService.fetchHashImage(imageUrl);
        
        assertNull(result);
        
        verify(looterScrapingErrorHandler).formatErrorItem(Constantes.HASH_IMAGE_ITEM, imageUrl);
        verify(looterScrapingErrorHandler).handle(
            any(RuntimeException.class),
            eq(Constantes.HASH_IMAGE_FETCH_CONTEXT),
            eq(Constantes.FETCH_DATA_ACTION),
            eq(formattedItem)
        );
    }

    @Test
    void fetchHashImage_ShouldHandleException_WhenRestTemplateThrowsException() throws Exception {
        String imageUrl = "https://example.com/image.jpg";
        String apiUrl = "https://iris-api.com/hash";
        RuntimeException apiException = new RuntimeException("API call failed");
        String formattedItem = "formatted-error-item";
        
        when(irisApiUrlBuilder.buildUrl("/hash")).thenReturn(apiUrl);
        when(requestFactory.createHashImageRequest(imageUrl)).thenReturn(requestEntity);
        when(restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, JsonNode.class))
            .thenThrow(apiException);
        when(looterScrapingErrorHandler.formatErrorItem(Constantes.HASH_IMAGE_ITEM, imageUrl))
            .thenReturn(formattedItem);
        
        JsonNode result = irisApiService.fetchHashImage(imageUrl);
        
        assertNull(result);
        
        verify(looterScrapingErrorHandler).formatErrorItem(Constantes.HASH_IMAGE_ITEM, imageUrl);
        verify(looterScrapingErrorHandler).handle(
            eq(apiException),
            eq(Constantes.HASH_IMAGE_FETCH_CONTEXT),
            eq(Constantes.FETCH_DATA_ACTION),
            eq(formattedItem)
        );
    }
}