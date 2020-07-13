package com.codefathers.cfkclient.dtos.product;

import lombok.Data;

import java.util.HashMap;

@Data
public class FilterSortDto {
    private int upPriceLimit;
    private int downPriceLimit;
    private int categoryId;
    private HashMap<String, String> activeFilters;
    private boolean offMode;
    private boolean availableOnly;

    private boolean isAscending;
    private SortType sortType;
}
