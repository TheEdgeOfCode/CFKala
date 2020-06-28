package com.codefathers.anonymous_bank.model.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table
public class Receipt {
    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    @Enumerated(EnumType.STRING)
    ReceiptType type;
    int sourceAccount;
    int destAccount;
    String description;
    long money;
    boolean paid;
}

enum ReceiptType{
    MOVE,
    WITHDRAW,
    DEPOSIT
}
