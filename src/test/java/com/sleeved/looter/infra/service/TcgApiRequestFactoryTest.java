package com.sleeved.looter.infra.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(SpringExtension.class)
public class TcgApiRequestFactoryTest {
  private TcgApiRequestFactory tcgApiRequestFactory;
  private final String TEST_API_KEY = "test-api-key-123456";

  @BeforeEach
  void setUp() {
    tcgApiRequestFactory = new TcgApiRequestFactory();
    // Injecter la valeur du apiKey manuellement pour le test
    ReflectionTestUtils.setField(tcgApiRequestFactory, "apiKey", TEST_API_KEY);
  }

  @Test
  void createAuthorizedRequest_shouldReturnEntityWithAuthorizationHeader() {
    // When
    HttpEntity<String> request = tcgApiRequestFactory.createAuthorizedRequest();

    // Then
    assertThat(request).isNotNull();

    HttpHeaders headers = request.getHeaders();
    assertThat(headers).isNotNull();

    assertThat(headers.containsKey("Authorization"))
        .as("Headers should contain Authorization key")
        .isTrue();

    assertThat(headers.get("Authorization"))
        .as("Should have exactly one Authorization value")
        .hasSize(1);

    assertThat(headers.getFirst("Authorization"))
        .as("Authorization header should contain the API key")
        .isEqualTo("Bearer " + TEST_API_KEY);

    assertThat(request.getBody())
        .as("Request body should be null as we're only setting headers")
        .isNull();
  }
}
