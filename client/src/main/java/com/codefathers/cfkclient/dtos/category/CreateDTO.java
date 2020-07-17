package com.codefathers.cfkclient.dtos.category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateDTO {
    private String name;
    private int parent;
    private List<String> feature;
}
