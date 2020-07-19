package com.codefathers.cfkclient.dtos.product;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor

public class FullProductPM {
    private MiniProductDto product;
    private Map<String, String> features;

    public FullProductPM(MiniProductDto product, Map<String, String> features) {
        this.product = product;
        this.features = features;
    }
}