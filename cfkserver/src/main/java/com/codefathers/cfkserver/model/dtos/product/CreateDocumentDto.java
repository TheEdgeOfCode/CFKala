package com.codefathers.cfkserver.model.dtos.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.core.io.ByteArrayResource;

import java.io.InputStream;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateDocumentDto extends CreateProductDTO{
    private InputStream resource;
    private String name;
    private String format;
}
