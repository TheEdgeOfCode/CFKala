package com.codefathers.cfkserver.model.entities.user;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "t_cart")
public class Cart {
    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ElementCollection(targetClass = SubCart.class)
    @OneToMany(cascade = CascadeType.ALL)
    @Column(name = "SUB_CARTS")
    private List<SubCart> subCarts;

    @Column(name = "TOTAL_PRICE")
    private long totalPrice;

    private String discountCode;

    public Cart() {
        subCarts = new ArrayList<>();
        discountCode = "";
    }
}