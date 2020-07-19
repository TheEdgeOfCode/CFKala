package com.codefathers.cfkclient.dtos.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

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
