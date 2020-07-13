package com.codefathers.cfkserver.exceptions.model.cart;

public class NoSuchAProductInCart extends Exception {
    public NoSuchAProductInCart(int productId) {
        super(String.valueOf(productId));
    }

    public NoSuchAProductInCart() {
    }
}
