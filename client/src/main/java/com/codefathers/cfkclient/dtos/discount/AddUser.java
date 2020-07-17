package com.codefathers.cfkclient.dtos.discount;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddUser {
    private String username;
    private String code;
    private int amount;
}
