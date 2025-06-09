package com.sleeved.looter.infra.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class IrisApiUrlBuilder {

  @Value("${iris.api.url.protocole:http}")
  private String protocole;

  @Value("${iris.api.url.domain:localhost:8083}")
  private String domain;

  @Value("${iris.api.url.base:api/v1}")
  private String baseUrl;

  @Value("${iris.api.url.endpoint.hash:images/hash/url}")
  private String hashEndpoint;

  public String buildUrl() {
    return String.format("%s://%s/%s/%s",
        protocole, domain, baseUrl, hashEndpoint);
  }

}
