package com.rubinho.vkproxy.services;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProxyService {
    private final RestTemplate restTemplate;
    private final String URL = "https://jsonplaceholder.typicode.com/";


    @Cacheable("request")
    public ResponseEntity<String> proxyingGetRequest(String request) {
        return ResponseEntity.ok(restTemplate.getForObject(URL + request, String.class));
    }

    @Cacheable("request")
    public ResponseEntity<String> proxyingPostRequest(String request, Map<String, Object> lookupRequestObject) {
        HttpHeaders headers = getHeaders();

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(lookupRequestObject, headers);

        return restTemplate.postForEntity(URL + request, entity, String.class);
    }

    @CachePut("request")
    public ResponseEntity<String> proxyingPutRequest(String request,
                                                     Map<String, Object> lookupRequestObject,
                                                     Long id) {
        HttpHeaders headers = getHeaders();

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(lookupRequestObject, headers);

        return restTemplate.exchange(URL + request, HttpMethod.PUT, entity, String.class, id);
    }

    @CacheEvict("request")
    public void proxyingDeleteRequest(String request,
                                      Long id) {
        restTemplate.delete(URL + request, id);
    }


    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }

    public String formatRequest(String request) {
        return request.replace("/api/", "");
    }

    public Long getIdFromURI(String URI) {
        String[] parts = URI.split("/");
        return Long.parseLong(parts[parts.length - 1]);
    }
}
