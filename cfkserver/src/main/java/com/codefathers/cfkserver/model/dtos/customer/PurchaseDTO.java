package com.codefathers.cfkserver.model.dtos.customer;

import com.codefathers.cfkserver.model.dtos.bank.PaymentType;
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
    private PaymentType paymentType;
}
