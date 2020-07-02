package com.codefathers.cfkserver.model.entities.user;


import com.codefathers.cfkserver.model.entities.product.Product;
import lombok.*;

import javax.persistence.*;

@Data @NoArgsConstructor
@Entity
public class SubCart{
    @Setter(AccessLevel.NONE)
    @Id @GeneratedValue
    private int id;

    @OneToOne
    private Product product;

    @OneToOne(cascade = CascadeType.ALL)
    private Seller seller;

    private int amount;

    @ManyToOne(cascade = CascadeType.ALL)
    private Cart cart;

    public SubCart(Product product, Seller seller, int amount) {
        this.product = product;
        this.seller = seller;
        this.amount = amount;
    }
}
