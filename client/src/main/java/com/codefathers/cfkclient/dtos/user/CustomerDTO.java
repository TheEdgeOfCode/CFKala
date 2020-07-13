package com.codefathers.cfkclient.dtos.user;

import lombok.Data;

@Data
public class CustomerDTO extends UserDTO {
    private long balance;
}
