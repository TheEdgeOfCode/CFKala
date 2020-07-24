package com.codefathers.cfkserver.model;

import com.codefathers.cfkserver.model.entities.product.Company;
import com.codefathers.cfkserver.model.entities.user.Cart;
import com.codefathers.cfkserver.model.entities.user.Customer;
import com.codefathers.cfkserver.model.entities.user.Seller;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class CustomerService {

    private Customer mohammad;
    private Company adidas;
    private Seller ali;

    @Before
    public void initialize() {
        mohammad = new Customer(
                "hatam008",
                "hatam009",
                "Ali",
                "Hatami",
                "hatam008@gmail.com",
                "0912 133 1232",
                new Cart(),
                20000
        );
        adidas = new Company("Adidas","115", "Clothing");
        ali = new Seller(
                "marmofayezi",
                "marmof.ir",
                "Cyrus",
                "Statham",
                "marmof@gmail.com",
                "+1 992 1122",
                new Cart(),
                adidas,
                0
        );
    }

    @Test
    public void purchaseTest() {

    }
}
