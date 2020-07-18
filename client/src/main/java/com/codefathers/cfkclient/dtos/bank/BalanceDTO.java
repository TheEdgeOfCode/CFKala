package com.codefathers.cfkclient.dtos.bank;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class BalanceDTO {
    private String username;
    private String password;
    private String token;
}
