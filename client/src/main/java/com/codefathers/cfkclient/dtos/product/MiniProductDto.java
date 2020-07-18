package com.codefathers.cfkclient.dtos.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MiniProductDto {
    private String name;
    private int id;
    private String categoryName;
    private String brand;
    private double score;
    private List<SellPackageDto> sellPackages;
    private String description;
    private boolean available;

    @Override
    public String toString() {
        return name;
    }

    public SellPackageDto findPackageForSeller(String username) {
        for (SellPackageDto SellPackageDto : sellPackages) {
            if (SellPackageDto.getSellerUsername().equals(username)) return SellPackageDto;
        }
        return null;
    }

    public int getPrice() {
        int price = 20000000;
        for (SellPackageDto pm : sellPackages) {
            if (pm.getPrice() < price) price = pm.getPrice();
        }
        return price == 20000000 ? -1 : price;
    }

    public int getOffPrice() {
        int ofPrc = 20000000;
        for (SellPackageDto pm : sellPackages) {
            if (pm.getOffPercent() != 0) {
                int price = pm.getPrice() * (100 - pm.getOffPercent()) / 100;
                if (ofPrc > price) {
                    ofPrc = price;
                }
            }
        }
        return ofPrc == 20000000 ? 0 : ofPrc;
    }
}
