package com.codefathers.cfkclient.dtos.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@Data @AllArgsConstructor
@NoArgsConstructor

public class CreateProductDTO {
    private String sellerName;
    private String productName;
    private int companyId;
    private int categoryId;
    private String description;
    private int amount;
    private int price;
    private HashMap<String,String> publicFeatures;
    private HashMap<String,String> specialFeature;
}
