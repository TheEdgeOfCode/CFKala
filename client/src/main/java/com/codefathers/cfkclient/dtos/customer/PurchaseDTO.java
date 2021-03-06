package com.codefathers.cfkclient.dtos.customer;

import com.codefathers.cfkclient.dtos.bank.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor
@NoArgsConstructor
public class PurchaseDTO {
    private String Address;
    private String zipCode;
    private String cardNumber;
    private String cardPassword;
    private String disCodeId;
    private String token;
    private PaymentType paymentType;
}
