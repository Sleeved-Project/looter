package com.sleeved.looter.infra.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
public class IrisApiRequestFactoryTest {

    @InjectMocks
    private IrisApiRequestFactory irisApiRequestFactory;
    
    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    private final String TEST_IMAGE_URL = "https://example.com/image.jpg";

    @Test
    void createHashImageRequest_shouldReturnEntityWithCorrectHeaders() throws JsonProcessingException {
        HttpEntity<String> request = irisApiRequestFactory.createHashImageRequest(TEST_IMAGE_URL);

        assertThat(request).isNotNull();

        HttpHeaders headers = request.getHeaders();
        assertThat(headers).isNotNull();

        assertThat(headers.getContentType())
            .as("Content-Type should be APPLICATION_JSON")
            .isEqualTo(MediaType.APPLICATION_JSON);
    }

    @Test
    void createHashImageRequest_shouldReturnEntityWithCorrectJsonBody() throws Exception {
        HttpEntity<String> request = irisApiRequestFactory.createHashImageRequest(TEST_IMAGE_URL);

        assertThat(request.getBody())
            .as("Request body should not be null")
            .isNotNull();

        // Parse le JSON pour vérifier la structure
        JsonNode jsonBody = objectMapper.readTree(request.getBody());
        
        assertThat(jsonBody.has("url"))
            .as("JSON body should contain 'url' field")
            .isTrue();

        assertThat(jsonBody.get("url").asText())
            .as("URL field should contain the provided image URL")
            .isEqualTo(TEST_IMAGE_URL);

        assertThat(jsonBody.size())
            .as("JSON body should contain exactly one field")
            .isEqualTo(1);
    }

    @Test
    void createHashImageRequest_shouldThrowException_whenImageUrlIsNull() {
        assertThatThrownBy(() -> irisApiRequestFactory.createHashImageRequest(null))
            .as("Should throw IllegalArgumentException when imageUrl is null")
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Image URL cannot be null");
    }
}