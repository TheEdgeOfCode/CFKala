package com.codefathers.cfkserver.model.entities.product;

import com.codefathers.cfkserver.model.entities.offs.Off;
import com.codefathers.cfkserver.model.entities.user.Seller;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "t_sell_package")
@Data
@NoArgsConstructor
public class SellPackage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @ManyToOne(cascade = CascadeType.ALL)
    private Product product;

    @ManyToOne(cascade = CascadeType.ALL)
    private Seller seller;
    private int price;
    private int stock;

    @ManyToOne
    private Off off;
    private boolean isOnOff;
    private boolean isAvailable;

    public SellPackage(Product product, Seller seller, int price, int stock, Off off, boolean isOnOff, boolean isAvailable) {
        this.product = product;
        this.seller = seller;
        this.price = price;
        this.stock = stock;
        this.off = off;
        this.isOnOff = isOnOff;
        this.isAvailable = isAvailable;
    }

    public boolean isForProductWithId(int id) {
        return product.getId() == id;
    }
}