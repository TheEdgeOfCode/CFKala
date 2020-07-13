package com.codefathers.cfkserver.model.dtos.product;

import com.codefathers.cfkserver.service.Sorter;
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
    private Sorter.SortType sortType;
}
