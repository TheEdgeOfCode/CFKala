package com.codefathers.cfkserver.exceptions.model.auction;

public class AuctionNotFoundException extends Exception {
    public AuctionNotFoundException() {
    }

    public AuctionNotFoundException(String message) {
        super(message);
    }
}
