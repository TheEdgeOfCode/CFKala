package com.codefathers.cfkserver.model.entities.request.edit;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class CategoryEditAttribute extends EditAttributes {
    String name;
    String addFeature;
    String removeFeature;
    int newParentId;
}