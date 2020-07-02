package com.codefathers.cfkserver.model.entities.product;

import lombok.Data;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "t_category")
public class Category {
    @Setter @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String categoryId;

    @ElementCollection
    private List<String> specialFeatures;

    @ElementCollection(targetClass = Category.class)
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Category> subCategories;

    @ManyToOne
    private Category parent;

    @ElementCollection(targetClass = Product.class)
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> allProducts;

    public Category(String name, Category parent) {
        this.name = name;
        this.parent = parent;
        this.specialFeatures = new ArrayList<>();
        this.subCategories  = new ArrayList<>();
        this.allProducts = new ArrayList<>();
    }

    public Category(){
        this.specialFeatures = new ArrayList<>();
        this.subCategories  = new ArrayList<>();
        this.allProducts = new ArrayList<>();
    }

}
