package com.rubinho.vkproxy.services;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.StringJoiner;

@ExtendWith(MockitoExtension.class)
public class ProxyServiceTests {
    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ProxyService proxyService;

    private String getRequest() {
        StringJoiner request = new StringJoiner("\n");
        request
                .add("{")
                .add("'userId': 1,")
                .add("'id': 2,")
                .add("'tile': test,")
                .add("'body': test")
                .add("}");
        return request.toString();

    }

    @Test
    public void proxyingGetRequest_Valid_returnsResponseEntity(){
        Mockito.when(restTemplate.getForObject(Mockito.anyString(), Mockito.any())).thenReturn(getRequest());
        ResponseEntity<String> response = proxyService.proxyingGetRequest("/api/test");
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

}
