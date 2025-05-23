package com.sleeved.looter.infra.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.sleeved.looter.common.util.Constantes;

@Component
public class TcgApiUrlBuilder {

  @Value("${tcgplayer.api.url.protocole}")
  private String apiProtocole;

  @Value("${tcgplayer.api.url.domain}")
  private String apiDomain;

  @Value("${tcgplayer.api.url.base}")
  private String apiBaseUrl;

  public String buildPaginatedUrl(String endpoint, Integer page, Integer pageSize) {
    String paginatedEndpoint = String.format(endpoint, page, pageSize);
    return buildUrl(paginatedEndpoint);
  }

  public String buildUrl(String endpoint) {
    String tcgPlayerUrl = String.format(Constantes.API_URL_FORMAT, apiProtocole, apiDomain);
    return String.format(Constantes.TCG_API_URL_BASE_FORMAT, tcgPlayerUrl, apiBaseUrl, endpoint);
  }

}
