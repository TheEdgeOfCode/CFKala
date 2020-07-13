package com.codefathers.cfkserver.model.dtos.product;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SellPackageDto {
    private int offPercent;
    private int price;
    private int stock;
    private String sellerUsername;
    private boolean isAvailable;

    @Override
    public String toString() {
        return sellerUsername;
    }
}
