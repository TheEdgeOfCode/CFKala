package com.codefathers.cfkserver.model.entities.user;

import com.codefathers.cfkserver.exceptions.model.user.NoSuchAPackageException;
import com.codefathers.cfkserver.model.entities.logs.SellLog;
import com.codefathers.cfkserver.model.entities.offs.Off;
import com.codefathers.cfkserver.model.entities.product.Company;
import com.codefathers.cfkserver.model.entities.product.SellPackage;
import com.codefathers.cfkserver.model.entities.request.Request;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Seller extends User {
    @ManyToOne
    @JoinColumn
    private Company company;

    private long balance;

    @ElementCollection(targetClass = Off.class)
    @OneToMany
    private List<Off> offs;

    @ElementCollection(targetClass = SellLog.class)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SellLog> sellLogs;

    @OneToMany
    private List<SellPackage> packages;

    @Column
    private boolean verified;

    @ElementCollection
    @OneToMany
    private List<Request> requests;

    public Seller(String username, String password, String firstName, String lastName, String email, String phoneNumber, Cart cart, Company company, long balance, String accountId) {
        super(username, password, firstName, lastName, email, phoneNumber, cart, accountId);
        this.company = company;
        this.balance = balance;
        this.offs = new ArrayList<>();
        this.sellLogs = new ArrayList<>();
        this.packages = new ArrayList<>();
        this.requests = new ArrayList<>();
    }

    public Seller(){
        this.offs = new ArrayList<>();
        this.sellLogs = new ArrayList<>();
        this.packages = new ArrayList<>();
        this.requests = new ArrayList<>();
    }

    public boolean getVerified(){
        return verified;
    }

    public boolean equals(Seller seller) {
        return this.username.equals(seller.username);
    }

    public void addRequest(Request request) {
        // TODO: 7/2/2020
        /*DBManager.save(request);
        requests.add(request);
        DBManager.save(this);*/
    }

    public SellPackage findPackageByProductId(int id) throws NoSuchAPackageException {
        for (SellPackage aPackage : packages) {
            if (aPackage.isForProductWithId(id)) return aPackage;
        }
        throw new NoSuchAPackageException("There Isn't Any Package with Id(" + id + ")" );
    }
}