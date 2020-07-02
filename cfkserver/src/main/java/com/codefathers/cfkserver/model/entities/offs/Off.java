package com.codefathers.cfkserver.model.entities.offs;

import com.codefathers.cfkserver.model.entities.product.Product;
import com.codefathers.cfkserver.model.entities.user.Seller;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
public class Off {
    @Setter(AccessLevel.NONE)
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int offId;

    @OneToOne
    private Seller seller;

    // TODO: 7/2/2020 Use SellPackage Instead
    @ElementCollection(targetClass = Product.class)
    @OneToMany
    private List<Product> products;

    @Enumerated(EnumType.STRING)
    private OffStatus offStatus;

    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;

    @Temporal(TemporalType.TIMESTAMP)
    private Date endTime;

    private int offPercentage;

    public Off(){
        products = new ArrayList<>();
    }
}