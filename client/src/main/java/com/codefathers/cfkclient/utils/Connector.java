package com.codefathers.cfkclient.utils;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class Connector {
    private String token;
    private RestTemplate restTemplate;

    public Connector(){
        // TODO: 7/14/2020 fill token
        restTemplate = new RestTemplate();
    }

    private <T,U> ResponseEntity<U> post(String uri,T dto ,Class<U> type){
        HttpEntity<T> requestEntity = new HttpEntity<>(dto);
        return restTemplate.exchange(uri, HttpMethod.POST,requestEntity,type);
    }
}
