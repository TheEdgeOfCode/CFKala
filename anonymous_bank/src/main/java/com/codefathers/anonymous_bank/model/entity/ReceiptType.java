package com.codefathers.anonymous_bank.model.entity;

public enum ReceiptType{
    MOVE,
    WITHDRAW,
    DEPOSIT;

    public static ReceiptType from(String string){
        switch (string){
            case "move" : return MOVE;
            case "deposit": return DEPOSIT;
            case "withdraw": return WITHDRAW;
        }
        return null;
    }
}
