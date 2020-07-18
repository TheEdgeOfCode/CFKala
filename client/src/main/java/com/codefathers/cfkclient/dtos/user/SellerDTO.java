package com.codefathers.cfkclient.dtos.user;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SellerDTO extends UserDTO {
    private int companyID;
    private long balance;

    public SellerDTO(String username, String password, String firstName, String lastName, String email, String phoneNumber, long balance) {
        super(username, password, firstName, lastName, email, phoneNumber);
        this.balance = balance;
    }
}
