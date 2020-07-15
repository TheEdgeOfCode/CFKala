package com.codefathers.cfkclient.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserFullDTO extends UserDTO {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String role;

    @Override
    public String toString() {
        return username;
    }
}
