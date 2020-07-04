package com.codefathers.cfkserver.model.dtos.product;

import lombok.Data;

import java.util.HashMap;

@Data
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
