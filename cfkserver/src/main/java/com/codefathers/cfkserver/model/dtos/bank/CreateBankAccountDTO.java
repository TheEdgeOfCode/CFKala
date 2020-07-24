package com.codefathers.cfkserver.model.dtos.bank;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateBankAccountDTO {
    private String username;
    private String firstName;
    private String lastName;
    private String password;
    private String rePassword;
}
