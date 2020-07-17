package com.codefathers.cfkclient.dtos.edit;

import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
public class CategoryEditAttribute extends EditAttributes {
    String name;
    String addFeature;
    String removeFeature;
    int newParentId;
}