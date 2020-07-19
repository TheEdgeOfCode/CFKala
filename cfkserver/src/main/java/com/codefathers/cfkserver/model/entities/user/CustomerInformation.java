package com.codefathers.cfkserver.model.entities.user;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data @NoArgsConstructor
@Entity
public class CustomerInformation {
    @Id @GeneratedValue(strategy = GenerationType.TABLE)
    private int id;
    private String address;
    private String zipCode;
    private String cardNumber;
    private String cardPassword;
    private String token;

    public CustomerInformation(String address, String zipCode, String cardNumber, String cardPassword, String token) {
        this.address = address;
        this.zipCode = zipCode;
        this.cardNumber = cardNumber;
        this.cardPassword = cardPassword;
        this.token = token;
    }
}