package com.codefathers.cfkserver.model.dtos.bank;

public enum ReceiptType {
    MOVE,
    WITHDRAW,
    DEPOSIT;

    public static String from(ReceiptType type){
        switch (type){
            case MOVE : return "move";
            case DEPOSIT : return "deposit";
            case WITHDRAW : return "withdraw";
        }
        return null;
    }
}
