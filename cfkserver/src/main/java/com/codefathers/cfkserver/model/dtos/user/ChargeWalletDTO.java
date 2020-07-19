package com.codefathers.cfkserver.model.dtos.user;

import com.codefathers.cfkserver.model.entities.user.Role;
import lombok.Data;

@Data
public class ChargeWalletDTO {
    private String token;
    private long money;
    private Role role;

    public ChargeWalletDTO(long money) {
        this.money = money;
    }
}
