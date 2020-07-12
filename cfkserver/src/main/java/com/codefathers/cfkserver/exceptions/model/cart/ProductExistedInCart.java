package com.codefathers.cfkserver.exceptions.model.cart;

public class ProductExistedInCart extends Exception {
    public ProductExistedInCart(long productId) {
        super(String.valueOf(productId));
    }

    public ProductExistedInCart() {
    }
}
