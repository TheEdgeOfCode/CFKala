package com.codefathers.cfkclient.dtos.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@Data
@AllArgsConstructor
public class FilterSortDto {
    private int upPriceLimit;
    private int downPriceLimit;
    private int categoryId;
    private HashMap<String, String> activeFilters;
    private boolean offMode;
    private boolean availableOnly;
    private String name;
    private String seller;
    private String brand;

    public FilterSortDto(){
        activeFilters = new HashMap<>();
        offMode = false;
        upPriceLimit = 0;
        downPriceLimit = 0;
    }
    private boolean isAscending;
    private SortType sortType;
}
