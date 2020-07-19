package com.codefathers.cfkclient.dtos.user;

import lombok.Data;

@Data
public class ChargeWalletDTO {
    private String token;
    private long money;
    private Role role;

    public ChargeWalletDTO(long money, Role role) {
        this.money = money;
    }
}
