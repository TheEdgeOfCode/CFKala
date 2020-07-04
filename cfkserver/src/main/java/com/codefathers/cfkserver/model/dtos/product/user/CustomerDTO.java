package com.codefathers.cfkserver.model.dtos.product.user;

import lombok.Data;

@Data
public class CustomerDTO extends UserDTO {
    private long balance;
}
