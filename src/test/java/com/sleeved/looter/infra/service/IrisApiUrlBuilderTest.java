package com.sleeved.looter.infra.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.test.util.ReflectionTestUtils;

public class IrisApiUrlBuilderTest {

  @InjectMocks
  private IrisApiUrlBuilder irisApiUrlBuilder;

  private final String TEST_PROTOCOLE = "http";
  private final String TEST_DOMAIN = "localhost:8083";
  private final String TEST_BASE_URL = "api/v1";
  
  private final String TEST_HASH_ENDPOINT = "images/hash/url";

  @BeforeEach
  void setUp() {
    irisApiUrlBuilder = new IrisApiUrlBuilder();
    ReflectionTestUtils.setField(irisApiUrlBuilder, "protocole", TEST_PROTOCOLE);
    ReflectionTestUtils.setField(irisApiUrlBuilder, "domain", TEST_DOMAIN);
    ReflectionTestUtils.setField(irisApiUrlBuilder, "baseUrl", TEST_BASE_URL);
  }

  @Test
  void buildUrl_shouldConstructCompleteUrl() {
    String expectedUrl = "http://localhost:8083/api/v1/images/hash/url";
    
    String actualUrl = irisApiUrlBuilder.buildUrl(TEST_HASH_ENDPOINT);
    
    assertThat(actualUrl)
        .as("Le buildUrl devrait construire l'URL complète pour l'API Iris")
        .isEqualTo(expectedUrl);
  }
}