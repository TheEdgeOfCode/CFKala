package com.codefathers.cfkserver.model.entities.logs;

import com.codefathers.cfkserver.model.entities.product.SoldProduct;
import com.codefathers.cfkserver.model.entities.user.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Setter
@Getter
@Entity
@Table(name = "t_sell_log")
public class SellLog extends Log {
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn
    private SoldProduct product;

    private int moneyGotten;
    private int discount;

    @OneToOne
    private User buyer;

    public SellLog(SoldProduct product, int moneyGotten, int discount, User buyer, Date date, DeliveryStatus deliveryStatus) {
        super(date, deliveryStatus);
        this.product = product;
        this.moneyGotten = moneyGotten;
        this.discount = discount;
        this.buyer = buyer;
    }

    public SellLog(){

    }
}