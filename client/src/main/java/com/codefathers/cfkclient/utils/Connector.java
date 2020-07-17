package com.codefathers.cfkclient.utils;

import com.codefathers.cfkclient.dtos.edit.UserEditAttributes;
import com.codefathers.cfkclient.dtos.product.FilterSortDto;
import com.codefathers.cfkclient.dtos.product.MiniProductArrayListDto;
import com.codefathers.cfkclient.dtos.product.MiniProductDto;
import com.codefathers.cfkclient.dtos.user.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.springframework.http.HttpMethod.*;

public class Connector {
    private String token;
    private RestTemplate restTemplate;

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

    public List<MiniProductDto> getAllProducts(FilterSortDto dto) throws Exception {
        ResponseEntity<MiniProductArrayListDto> response = post("http://127.0.0.1:8050/product/get_all_products",
                dto, MiniProductArrayListDto.class);
        return Objects.requireNonNull(response.getBody()).getDtos();
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
        ResponseEntity responses = post("http://127.0.0.1:8050/users/edit", attributes, HttpStatus.class);
    }
}
