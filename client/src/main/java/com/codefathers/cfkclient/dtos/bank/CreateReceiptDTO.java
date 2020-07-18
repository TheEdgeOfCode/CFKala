package com.codefathers.cfkclient.dtos.bank;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateReceiptDTO {
    private String token;
    private ReceiptType type;
    private long money;
    private int source;
    private int dest;
    private String description;
}
