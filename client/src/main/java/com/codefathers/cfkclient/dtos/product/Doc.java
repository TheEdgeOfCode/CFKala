package com.codefathers.cfkclient.dtos.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Doc {
    private int id;
    private String name;
    private String extension;
    private long size;
}
