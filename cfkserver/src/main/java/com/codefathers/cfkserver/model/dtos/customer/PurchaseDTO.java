package com.codefathers.cfkserver.model.dtos.customer;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class PurchaseDTO {
    private String Address;
    private String zipCode;
    private String cardNumber;
    private String cardPassword;
    private String disCodeId;
    private String token;
}
