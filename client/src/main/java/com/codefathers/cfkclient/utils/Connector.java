package com.codefathers.cfkclient.utils;

import com.codefathers.cfkclient.dtos.content.AdPM;
import com.codefathers.cfkclient.dtos.content.MainContent;
import com.codefathers.cfkclient.dtos.edit.UserEditAttributes;
import com.codefathers.cfkclient.dtos.product.FilterSortDto;
import com.codefathers.cfkclient.dtos.product.MiniProductArrayListDto;
import com.codefathers.cfkclient.dtos.product.MiniProductDto;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.boot.json.GsonJsonParser;
import com.codefathers.cfkclient.dtos.user.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Type;
import java.util.*;

import static org.springframework.http.HttpMethod.*;

public class Connector {
    private String token;
    private RestTemplate restTemplate;
    private static Connector connector = new Connector();

    public static Connector getInstance() {
        return connector;
    }

    public Connector(){
        token = "";
        restTemplate = new RestTemplate();
    }

    private <T,U> ResponseEntity<U> post(String uri,T dto ,Class<U> type) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authentication","cfk! " + token);
        HttpEntity<T> requestEntity = new HttpEntity<>(dto,headers);
        ResponseEntity<U> response = restTemplate.exchange(uri, POST, requestEntity, type);
        if (response.getStatusCode().equals(HttpStatus.OK)){
            return response;
        }else {
            /*this might cause problem */
            String error = response.getStatusCode().toString() +  response.getHeaders().get("ERROR");
            throw new Exception(error);
        }
    }

    private <T,U> U get(String uri, T dto , Type type) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authentication","cfk! " + token);
        HttpEntity<T> requestEntity = new HttpEntity<>(dto,headers);
        ResponseEntity<String> response = restTemplate.exchange(uri, GET, requestEntity, String.class);
        if (response.getStatusCode().equals(HttpStatus.OK)){
            Gson gson = new Gson();
            return gson.fromJson(response.getBody(),type);
        }else {
            throw new Exception(new GsonJsonParser().parseMap(response.getBody()).get("error").toString());
        }
    }

    public List<MiniProductDto> getAllProducts(FilterSortDto dto) throws Exception {
        ResponseEntity<MiniProductArrayListDto> response = post("http://127.0.0.1:8050/product/get_all_products",
                dto, MiniProductArrayListDto.class);
        return Objects.requireNonNull(response.getBody()).getDtos();
    }

    public List<MainContent> mainContents() throws Exception {
        return get("http://127.0.0.1:8050/content/get_main_contents",null,new TypeToken<ArrayList<MainContent>>(){}.getType());
    }

    public List<AdPM> getAds() throws Exception {
        return get("http://127.0.0.1:8050/content/all_ads",null,new TypeToken<ArrayList<AdPM>>(){}.getType());
    }

    public void login(LoginDto dto) throws Exception {
        ResponseEntity<TokenRoleDto> role = post("http://127.0.0.1:8050/users/login", dto, TokenRoleDto.class);
        token = Objects.requireNonNull(role.getBody()).getToken();
        //TODO: Impl
    }

    public void createCustomerAccount(CreateAccountDTO<CustomerDTO> dto) throws Exception {
        ResponseEntity<TokenRoleDto> role = post("http://127.0.0.1:8050/users/create_account", dto, TokenRoleDto.class);
        token = Objects.requireNonNull(role.getBody()).getToken();
        //TODO: Impl
    }

    public void createManagerAccount(CreateAccountDTO<ManagerDTO> dto) throws Exception {
        ResponseEntity<TokenRoleDto> role = post("http://127.0.0.1:8050/users/create_account", dto, TokenRoleDto.class);
        token = Objects.requireNonNull(role.getBody()).getToken();
        //TODO: Impl
    }
    public void createSellerAccount(CreateAccountDTO<SellerDTO> dto) throws Exception {
        ResponseEntity<TokenRoleDto> role = post("http://127.0.0.1:8050/users/create_account", dto, TokenRoleDto.class);
        //TODO: Impl
    }

    public UserFullDTO viewPersonalInfo() throws Exception {
        ResponseEntity<UserFullDTO> response = post("http://127.0.0.1:8050/users/view", null, UserFullDTO.class);
        return response.getBody();
    }

    public void editPersonalInfo(UserEditAttributes attributes) throws Exception {
        post("http://127.0.0.1:8050/users/edit", attributes, HttpStatus.class);
    }
}
