package com.codefathers.cfkserver.model.entities.maps;

import com.codefathers.cfkserver.model.entities.product.SoldProduct;
import lombok.*;

import javax.persistence.*;

@Data
@Entity
public class SoldProductSellerMap {
    @Setter(AccessLevel.NONE)
    @Id @GeneratedValue
    private int id;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn
    private SoldProduct soldProduct;
    private String seller;
    public boolean isProduct(int id){
        return soldProduct.getSourceId() == id;
    }
}