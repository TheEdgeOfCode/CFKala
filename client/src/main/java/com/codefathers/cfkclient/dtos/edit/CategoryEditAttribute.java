package com.codefathers.cfkclient.dtos.edit;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor

public class CategoryEditAttribute extends EditAttributes {
    String name;
    String addFeature;
    String removeFeature;
    int newParentId;
}