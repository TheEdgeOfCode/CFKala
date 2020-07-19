package com.codefathers.cfkclient.dtos.bank;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class BalanceDTO {
    private String username;
    private String password;
    private String token;

    public BalanceDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
