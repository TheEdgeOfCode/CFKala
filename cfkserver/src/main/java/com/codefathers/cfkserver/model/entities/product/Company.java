package com.codefathers.cfkserver.model.entities.product;

import lombok.AccessLevel;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
public class Company {
    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true)
    private String name;
    private String phone;
    @Column(name = "type")
    private String group;

    @ElementCollection(targetClass = Product.class)
    @OneToMany(cascade = CascadeType.ALL)
    private List<Product> productsIn;

    public Company(String name, String phone, String group) {
        this.name = name;
        this.phone = phone;
        this.group = group;
        this.productsIn = new ArrayList<>();
    }
}