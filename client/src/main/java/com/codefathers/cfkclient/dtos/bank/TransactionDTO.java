package com.codefathers.cfkclient.dtos.bank;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionDTO {
    private String receiptType;
    private long money;
    private int sourceAccountID;
    private int destAccountID;
    private String description;
    private boolean paid;
    private int receiptId;
}
