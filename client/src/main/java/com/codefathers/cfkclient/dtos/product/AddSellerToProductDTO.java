package com.codefathers.cfkclient.dtos.product;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddSellerToProductDTO {
    private int productId;
    private int amount;
    private int price;
}
