package com.codefathers.cfkserver.model.dtos.bank;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateReceiptDTO {
    private String username;
    private String password;
    private String token;
    private ReceiptType type;
    private long money;
    private String source;
    private String dest;
    private String description;
}
