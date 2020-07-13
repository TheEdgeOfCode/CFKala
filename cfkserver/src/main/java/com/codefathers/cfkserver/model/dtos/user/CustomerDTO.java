package com.codefathers.cfkserver.model.dtos.user;

import lombok.Data;

@Data
public class CustomerDTO extends UserDTO {
    private long balance;
}
