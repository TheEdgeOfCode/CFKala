package com.codefathers.cfkclient.dtos.customer;

import com.codefathers.cfkclient.dtos.product.MiniProductDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class InCartDTO {
    private MiniProductDto miniProductDto;
    private String productName;
    private String sellerId;
    private int price;
    private int offPrice;
    private int amount;
    private int total;
}
