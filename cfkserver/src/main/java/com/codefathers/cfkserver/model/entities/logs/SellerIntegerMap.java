package com.codefathers.cfkserver.model.entities.logs;

import com.codefathers.cfkserver.model.entities.user.Seller;
import lombok.*;

import javax.persistence.*;

@Data
@Entity
@Table(name = "t_map")
public class SellerIntegerMap{
    @Setter(AccessLevel.NONE)
    @Id @GeneratedValue
    private int id;

    @ManyToOne
    @JoinColumn(name = "SELLER")
    private Seller seller;
    @Column(name = "INTEGER_VALUE")
    private Integer integer;

    public boolean thisIsTheMapKey(String sellerId){
        return seller.getUsername().equals(sellerId);
    }

    public SellerIntegerMap(){}

    public SellerIntegerMap(Seller seller, Integer integer) {
        this.seller = seller;
        this.integer = integer;
    }
}