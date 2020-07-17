package com.codefathers.cfkclient.dtos.content;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdPM {
    int productId;
    String name;
    String seller;
    byte[] image;
}
