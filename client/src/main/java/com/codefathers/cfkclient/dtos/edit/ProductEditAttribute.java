package com.codefathers.cfkclient.dtos.edit;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor

public class ProductEditAttribute extends EditAttributes{
    private String name;
    private Map<String, String> publicFeatures;
    private Map<String, String> specialFeatures;
    private int newStock;
    private int newPrice;
    private int newCategoryId;
}

