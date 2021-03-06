package com.codefathers.anonymous_bank.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class TransactionDto {
    private String receiptType;
    private long money;
    private int sourceAccountID;
    private int destAccountID;
    private String description;
    private int paid;
}
