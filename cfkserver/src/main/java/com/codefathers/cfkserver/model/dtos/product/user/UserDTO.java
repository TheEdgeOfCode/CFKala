package com.codefathers.cfkserver.model.dtos.product.user;

import lombok.Data;

@Data
public class UserDTO {
    protected String username;
    protected String password;
    protected String firstName;
    protected String lastName;
    protected String email;
    protected String phoneNumber;
}
