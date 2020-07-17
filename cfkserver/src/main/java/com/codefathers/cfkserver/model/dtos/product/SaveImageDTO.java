package com.codefathers.cfkserver.model.dtos.product;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.InputStream;
import java.util.ArrayList;

@Data
@AllArgsConstructor
public class SaveImageDTO {
    private int productId;
    private InputStream mainImage;
    private ArrayList<InputStream> files;
}
