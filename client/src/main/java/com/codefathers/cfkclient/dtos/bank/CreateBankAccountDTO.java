package com.codefathers.cfkclient.dtos.bank;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateBankAccountDTO {
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String rePassword;
}
