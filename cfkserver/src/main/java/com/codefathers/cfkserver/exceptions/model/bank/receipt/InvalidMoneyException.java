package com.codefathers.cfkserver.exceptions.model.bank.receipt;

public class InvalidMoneyException extends Exception {
    public InvalidMoneyException() {
    }

    public InvalidMoneyException(String message) {
        super(message);
    }
}
