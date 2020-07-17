package com.codefathers.cfkserver.model.dtos.content;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.InputStream;

@Data
@AllArgsConstructor
public class AdPM {
    int productId;
    String name;
    String seller;
    byte[] image;
}
