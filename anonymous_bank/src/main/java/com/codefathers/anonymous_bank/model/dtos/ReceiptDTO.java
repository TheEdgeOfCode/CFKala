package com.codefathers.anonymous_bank.model.dtos;

import com.codefathers.anonymous_bank.model.entity.ReceiptType;
import lombok.Data;

@Data
public class ReceiptDTO {
    private ReceiptType type;
    private long money;
    private int source;
    private int dest;
    private String description;
}
