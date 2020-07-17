package com.codefathers.anonymous_bank.model.dtos;

import com.codefathers.anonymous_bank.model.entity.ReceiptType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class TransactionDto {
    private int id;
    private ReceiptType receiptType;
    private long money;
    private int sourceAccountID;
    private int destAccountID;
    private String description;
    private boolean paid;
}
