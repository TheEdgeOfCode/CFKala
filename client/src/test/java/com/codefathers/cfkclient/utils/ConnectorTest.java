package com.codefathers.cfkclient.utils;

import com.codefathers.cfkclient.dtos.product.FilterSortDto;
import com.codefathers.cfkclient.dtos.product.MiniProductDto;
import com.codefathers.cfkclient.dtos.product.SortType;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConnectorTest {
    private static Connector connector = new Connector();

    @Test
    void getAllProduct() throws Exception {
        FilterSortDto dto = new FilterSortDto(0,0,0,new HashMap<>(),
                false,false,null,null,null,false, SortType.NAME);
        List<MiniProductDto> allProducts = connector.getAllProducts(dto);
        System.out.println(allProducts);
    }

    @Test
    void getAllContents() throws Exception {
        System.out.println(connector.mainContents());
    }
}