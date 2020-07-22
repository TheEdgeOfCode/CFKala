package com.codefathers.cfkclient.dtos.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.core.io.ByteArrayResource;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateDocumentDto extends CreateProductDTO{
    private ByteArrayResource resource;
    private long size;
    private long name;
}
