package com.codefathers.cfkserver.model.dtos.customer;

import com.codefathers.cfkserver.model.dtos.product.MiniProductDto;
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
