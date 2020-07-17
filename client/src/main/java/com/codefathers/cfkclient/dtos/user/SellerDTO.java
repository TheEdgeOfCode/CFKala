package com.codefathers.cfkclient.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
public class SellerDTO extends UserDTO {
    private Company company;
    private long balance;

    public SellerDTO(String username, String password, String firstName, String lastName, String email, String phoneNumber, long balance) {
        super(username, password, firstName, lastName, email, phoneNumber);
        this.balance = balance;
    }
}
