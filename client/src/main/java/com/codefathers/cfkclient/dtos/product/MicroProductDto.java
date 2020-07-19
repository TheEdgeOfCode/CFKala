package com.codefathers.cfkclient.dtos.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor
@NoArgsConstructor

public class MicroProductDto {
    private String name;
    private int id;

    @Override
    public String toString(){
        return name;
    }
}
