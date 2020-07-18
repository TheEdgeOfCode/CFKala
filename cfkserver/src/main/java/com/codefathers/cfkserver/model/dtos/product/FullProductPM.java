package com.codefathers.cfkserver.model.dtos.product;

import lombok.Data;

import java.util.Map;

@Data
public class FullProductPM {
    private MiniProductDto product;
    private Map<String, String> features;

    public FullProductPM(MiniProductDto product, Map<String, String> features) {
        this.product = product;
        this.features = features;
    }
}