package com.sleeved.looter.infra.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class TcgApiRequestFactory {

  @Value("${tcgplayer.api.key}")
  private String apiKey;

  public HttpEntity<String> createAuthorizedRequest() {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + apiKey);
    return new HttpEntity<>(headers);
  }

}
