package com.codefathers.cfkclient.dtos.user;

import lombok.Data;

@Data
public class ChargeWalletDTO {
    private String token;
    private long money;

    public ChargeWalletDTO(long money) {
        this.money = money;
    }
}
