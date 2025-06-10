package com.sleeved.looter.infra.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.sleeved.looter.common.util.Constantes;

@Component
public class IrisApiUrlBuilder {

  @Value("${iris.api.url.protocole:http}")
  private String protocole;

  @Value("${iris.api.url.domain:localhost:8083}")
  private String domain;

  @Value("${iris.api.url.base:api/v1}")
  private String baseUrl;

  public String buildUrl(String endpoint) {
    String irisUrl = String.format(Constantes.API_URL_FORMAT, protocole, domain);
    return String.format(Constantes.IRIS_API_URL_BASE_FORMAT, irisUrl, baseUrl, endpoint);
  }

}
