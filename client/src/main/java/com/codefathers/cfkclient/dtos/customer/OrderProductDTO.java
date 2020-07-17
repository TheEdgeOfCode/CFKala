package com.codefathers.cfkclient.dtos.customer;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class OrderProductDTO {
    private int id;
    private String name;
    private String sellerUsername;
    private long price;
}
