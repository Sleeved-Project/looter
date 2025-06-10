package com.sleeved.looter.infra.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class IrisApiRequestFactory {

    private final ObjectMapper objectMapper;

    public HttpEntity<String> createHashImageRequest(String imageUrl) throws JsonProcessingException {
        if (imageUrl == null) {
            throw new IllegalArgumentException("Image URL cannot be null");
        }
        
        HttpHeaders headers = createDefaultHeaders();
        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("url", imageUrl);
        
        return new HttpEntity<>(objectMapper.writeValueAsString(requestBody), headers);
    }

    private HttpHeaders createDefaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

}
