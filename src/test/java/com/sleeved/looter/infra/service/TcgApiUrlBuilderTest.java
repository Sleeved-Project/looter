package com.sleeved.looter.infra.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.sleeved.looter.common.util.Constantes;

public class TcgApiUrlBuilderTest {

  private TcgApiUrlBuilder tcgApiUrlBuilder;

  private final String TEST_PROTOCOLE = "https";
  private final String TEST_DOMAIN = "api.tcgplayer.com";
  private final String TEST_BASE_URL = "v2";

  @BeforeEach
  void setUp() {
    tcgApiUrlBuilder = new TcgApiUrlBuilder();

    ReflectionTestUtils.setField(tcgApiUrlBuilder, "apiProtocole", TEST_PROTOCOLE);
    ReflectionTestUtils.setField(tcgApiUrlBuilder, "apiDomain", TEST_DOMAIN);
    ReflectionTestUtils.setField(tcgApiUrlBuilder, "apiBaseUrl", TEST_BASE_URL);
  }

  @Test
  void buildUrl_shouldConstructCompleteUrl() {
    String endpoint = "endpoint/test";
    String expectedTcgPlayerUrl = String.format(Constantes.API_URL_FORMAT, TEST_PROTOCOLE, TEST_DOMAIN);
    String expectedUrl = String.format(Constantes.TCG_API_URL_BASE_FORMAT, expectedTcgPlayerUrl, TEST_BASE_URL,
        endpoint);

    String actualUrl = tcgApiUrlBuilder.buildUrl(endpoint);

    assertThat(actualUrl)
        .as("Le buildUrl devrait construire l'URL complète correctement")
        .isEqualTo(expectedUrl);
  }

  @Test
  void buildPaginatedUrl_shouldFormatEndpointAndBuildUrl() {
    String endpoint = "endpoint/products?page=%d&pageSize=%d";
    int page = 2;
    int pageSize = 100;

    String formattedEndpoint = String.format(endpoint, page, pageSize);
    String expectedTcgPlayerUrl = String.format(Constantes.API_URL_FORMAT, TEST_PROTOCOLE, TEST_DOMAIN);
    String expectedUrl = String.format(Constantes.TCG_API_URL_BASE_FORMAT, expectedTcgPlayerUrl, TEST_BASE_URL,
        formattedEndpoint);

    String actualUrl = tcgApiUrlBuilder.buildPaginatedUrl(endpoint, page, pageSize);

    assertThat(actualUrl)
        .as("Le buildPaginatedUrl devrait formater les paramètres de pagination correctement")
        .isEqualTo(expectedUrl)
        .contains("page=" + page)
        .contains("pageSize=" + pageSize);
  }
}
