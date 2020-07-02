package com.codefathers.cfkserver.model.entities.product;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class SoldProduct {
    @Id @GeneratedValue
    private int id;
    private int sourceId;
    private int soldPrice;
}
