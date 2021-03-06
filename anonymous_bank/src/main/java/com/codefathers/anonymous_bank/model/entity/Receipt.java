package com.codefathers.anonymous_bank.model.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table
@NoArgsConstructor
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

    public Receipt(ReceiptType type, int sourceAccount, int destAccount, long money) {
        this.type = type;
        this.sourceAccount = sourceAccount;
        this.destAccount = destAccount;
        this.money = money;
    }
}

