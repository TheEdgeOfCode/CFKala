package com.codefathers.cfkserver.model.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserFullDTO{
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String role;
    private String accountId;

    @Override
    public String toString() {
        return username;
    }
}
