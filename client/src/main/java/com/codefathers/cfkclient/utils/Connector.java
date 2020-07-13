package com.codefathers.cfkclient.utils;

import com.codefathers.cfkclient.dtos.product.FilterSortDto;
import com.codefathers.cfkclient.dtos.product.MiniProductArrayListDto;
import com.codefathers.cfkclient.dtos.product.MiniProductDto;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.springframework.http.HttpMethod.*;

public class Connector {
    private String token;
    private RestTemplate restTemplate;

    public Connector(){
        token = "";
        // TODO: 7/14/2020 fill token
        restTemplate = new RestTemplate();
    }

    private <T,U> ResponseEntity<U> post(String uri,T dto ,Class<U> type){
        HttpEntity<T> requestEntity = new HttpEntity<>(dto);
        requestEntity.getHeaders().add("Authentication","cfk! " + token);
        return restTemplate.exchange(uri, POST,requestEntity,type);
    }

    public List<MiniProductDto> getAllProducts(FilterSortDto dto){
        ResponseEntity<MiniProductArrayListDto> response = post("127.0.0.1:8050/product/get_all_products",
                dto, MiniProductArrayListDto.class);
        return response.getBody().getDtos(); // TODO: 7/14/2020 handle errors
    }
}
