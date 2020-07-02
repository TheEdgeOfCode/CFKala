package com.codefathers.cfkserver.model.entities.logs;

import com.codefathers.cfkserver.model.entities.maps.SoldProductSellerMap;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "t_purchase_log")
public class PurchaseLog extends Log {
    @ElementCollection(targetClass = SoldProductSellerMap.class)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn
    private List<SoldProductSellerMap> productsAndItsSellers;

    private int pricePaid;
    private int discount;

    public PurchaseLog(Date date, DeliveryStatus deliveryStatus, List<SoldProductSellerMap> productsAndItsSellers, int pricePaid, int discount) {
        super(date, deliveryStatus);
        this.productsAndItsSellers = productsAndItsSellers;
        this.pricePaid = pricePaid;
        this.discount = discount;
    }

    public PurchaseLog(){
        this.productsAndItsSellers = new ArrayList<>();
    }

    public boolean containsProduct(int id){
        for (SoldProductSellerMap map : productsAndItsSellers) {
            if (map.isProduct(id)) return true;
        }
        return false;
    }
}
