package com.codefathers.cfkclient.dtos.customer;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class PurchaseDTO {
    private String Address;
    private String zipCode;
    private String cardNumber;
    private String cardPassword;
    private String disCodeId;
}
