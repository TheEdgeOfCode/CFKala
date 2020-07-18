package com.codefathers.cfkserver.model.dtos.bank;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class NeededForTransactionDTO {
    private String username;
    private String password;
    private String token;
    private TransactType type;
}
