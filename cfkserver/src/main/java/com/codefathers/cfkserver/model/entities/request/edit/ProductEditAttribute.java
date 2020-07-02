package com.codefathers.cfkserver.model.entities.request.edit;


import lombok.*;
import javax.persistence.*;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class ProductEditAttribute extends EditAttributes{
    private String name;
    @ElementCollection
    private Map<String, String> publicFeatures;
    @ElementCollection
    private Map<String, String> specialFeatures;
    private int newStock;
    private int newPrice;
    private int newCategoryId;
}

