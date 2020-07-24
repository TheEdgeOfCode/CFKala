package com.codefathers.cfkclient.dtos.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.core.io.ByteArrayResource;

import java.io.InputStream;
import java.util.HashMap;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateDocumentDto extends CreateProductDTO{
    private ByteArrayResource resource;
    private String name;
    private String format;

    public CreateDocumentDto(String sellerName, String productName, int companyId, int categoryId, String description,
                             int amount, int price, HashMap<String, String> publicFeatures,
                             HashMap<String, String> specialFeature, ByteArrayResource resource, String name,
                             String format) {
        super(sellerName, productName, companyId, categoryId, description, amount, price, publicFeatures,
                specialFeature);
        this.resource = resource;
        this.name = name;
        this.format = format;
    }
}
