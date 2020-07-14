package com.codefathers.cfkserver.model.dtos.customer;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data @AllArgsConstructor
public class CartDTO {
    private List<InCartDTO> inCartDTOS;
    private long totalPrice;
}
