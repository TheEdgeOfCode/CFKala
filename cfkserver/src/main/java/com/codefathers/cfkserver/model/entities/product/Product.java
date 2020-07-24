package com.codefathers.cfkserver.model.entities.product;

import com.codefathers.cfkserver.exceptions.model.product.NoSuchSellerException;
import com.codefathers.cfkserver.model.entities.user.Seller;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.util.*;

@Data
@AllArgsConstructor
@Entity @Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    private ProductStatus productStatus;
    private String name;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    private Date dateAdded;

    @ManyToOne
    private Company companyClass;

    @ManyToOne
    private Category category;

    @ElementCollection
    @JoinTable
    @MapKeyColumn(name = "feature")
    private Map<String,String> publicFeatures;

    @ElementCollection
    @JoinTable
    @MapKeyColumn(name = "feature")
    private Map<String,String> specialFeatures;

    private String description;

    @ElementCollection(targetClass = Score.class)
    @OneToMany(cascade = CascadeType.ALL)
    private List<Score> allScores;

    private double totalScore;

    @ElementCollection(targetClass = Comment.class)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> allComments;

    @Column(name = "VIEW")
    private int view;

    private int boughtAmount;
    private int leastPrice;
    private boolean onOff;

    private boolean isFile;

    @OneToOne(cascade = CascadeType.ALL)
    private Document document;

    @OneToMany(cascade = {CascadeType.DETACH,CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH})
    private List<SellPackage> packages;

    public Product(int id){this.id = id;}

    public Product(String name, Company company, Category category, HashMap<String, String> publicFeatures, HashMap<String, String> specialFeatures, String description, SellPackage sellPackage) {
        this.name = name;
        this.companyClass = company;
        this.category = category;
        this.publicFeatures = publicFeatures;
        this.specialFeatures = specialFeatures;
        this.description = description;
        this.allComments = new ArrayList<>();
        this.productStatus = ProductStatus.UNDER_CREATION;
        this.dateAdded = new Date();
        this.allScores = new ArrayList<>();
        this.view = 0;
        this.boughtAmount = 0;
        this.totalScore = 0;
        this.packages = new ArrayList<>();
        this.packages.add(sellPackage);
    }

    public Product() {
        this.allScores = new ArrayList<>();
        this.allComments = new ArrayList<>();
    }

    public SellPackage findPackageBySeller(String username) throws NoSuchSellerException {
        for (SellPackage sellPackage : packages) {
            if (sellPackage.getSeller().getUsername().equals(username))return sellPackage;
        }
        throw new NoSuchSellerException();
    }

    public SellPackage findPackageBySeller(Seller seller) throws NoSuchSellerException{
        for (SellPackage sellPackage : packages) {
            if (sellPackage.getSeller().equals(seller))return sellPackage;
        }
        throw new NoSuchSellerException();
    }

    public boolean hasSeller(Seller seller){
        for (SellPackage sellPackage : packages) {
            if (sellPackage.getSeller().equals(seller))return true;
        }
        return false;
    }

    public boolean hasSeller(String username){
        for (SellPackage sellPackage : packages) {
            if (sellPackage.getSeller().getUsername().equals(username))return true;
        }
        return false;
    }

    public boolean isOnOff() {
        for (SellPackage sellPackage : packages) {
            if (sellPackage.isOnOff()) return true;
        }
        return false;
    }

    public boolean isAvailable() {
        if (packages != null)
            for (SellPackage aPackage : packages) {
                if (aPackage.getStock() != 0) return true;
            }
        return false;
    }
}