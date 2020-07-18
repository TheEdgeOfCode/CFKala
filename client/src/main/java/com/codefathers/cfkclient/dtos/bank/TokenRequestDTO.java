package com.codefathers.cfkclient.dtos.bank;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenRequestDTO {
    String username;
    String password;
}
