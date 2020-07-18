package com.codefathers.cfkserver.model.dtos.bank;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionDTO {
    private ReceiptType receiptType;
    private long money;
    private int sourceAccountID;
    private int destAccountID;
    private String description;
    private boolean paid;
}
