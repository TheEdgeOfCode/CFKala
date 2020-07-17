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
    private List<SellPackageDto> sellPackages;
    private String brand;
    private double score;
    private String description;
    private boolean available;

    @Override
    public String toString() {
        return name;
    }
}
