package com.codefathers.cfkclient.utils;

import com.codefathers.cfkclient.dtos.customer.*;
import com.codefathers.cfkclient.dtos.discount.DisCodeUserDTO;
import com.codefathers.cfkclient.dtos.discount.DisCodeUserListDTO;
import com.codefathers.cfkclient.dtos.product.FilterSortDto;
import com.codefathers.cfkclient.dtos.product.MiniProductArrayListDto;
import com.codefathers.cfkclient.dtos.product.MiniProductDto;
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

    public CartDTO showCart() throws Exception {
        ResponseEntity<CartDTO> response = post("http://127.0.0.1:8050/customer/show_cart",
                null, CartDTO.class);
        return Objects.requireNonNull(response.getBody());
    }

    // TODO : check void methods

    public void changeAmount(String info) throws Exception {
        post("http://127.0.0.1:8050/customer/change_amount", info, Void.class);
    }

    public List<InCartDTO> showProducts() throws Exception {
        ResponseEntity<InCartArrayListDTO> response = post("http://127.0.0.1:8050/customer/show_products",
                null, InCartArrayListDTO.class);
        return Objects.requireNonNull(response.getBody()).getInCartDTOS();
    }

    public void deleteProductFromCart(String info) throws Exception {
        post("http://127.0.0.1:8050/customer/delete_product_from_cart",
                info, Void.class);
    }

    public void purchase(PurchaseDTO dto) throws Exception {
        post("http://127.0.0.1:8050/customer/purchase",
                dto, Void.class);
    }

    public Long showPurchaseTotalPrice(String disCode) throws Exception {
        ResponseEntity<Long> response = post("http://127.0.0.1:8050/customer/purchase/show_total_price",
                disCode, Long.class);
        return Objects.requireNonNull(response).getBody();
    }

    public List<OrderLogDTO> showOrders() throws Exception {
        ResponseEntity<OrderLogListDTO> response = post("http://127.0.0.1:8050/customer/show_orders",
                null, OrderLogListDTO.class);
        return Objects.requireNonNull(response.getBody()).getDtos();
    }

    public void addViewDigest(String productId) throws Exception {
        post("http://127.0.0.1:8050/customer/add_view",
                productId, Void.class);
    }

    public List<DisCodeUserDTO> showDiscountCodes() throws Exception {
        ResponseEntity<DisCodeUserListDTO> response = post("http://127.0.0.1:8050/customer/show_discounts",
                null, DisCodeUserListDTO.class);
        return Objects.requireNonNull(response.getBody()).getDtos();
    }

    public void assignAScore(String info) throws Exception {
        post("http://127.0.0.1:8050/customer/assign_score",
                info, Void.class);
    }
}
