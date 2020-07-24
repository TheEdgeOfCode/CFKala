package com.codefathers.cfkclient.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class TakeMoneyDTO {
    private String token;
    private long money;
    private Role role;
}
