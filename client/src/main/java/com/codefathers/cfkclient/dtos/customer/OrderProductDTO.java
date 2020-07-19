package com.codefathers.cfkclient.dtos.customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor
@NoArgsConstructor

public class OrderProductDTO {
    private int id;
    private String name;
    private String sellerUsername;
    private long price;
}
