package com.codefathers.cfkserver.model.entities.user;

import com.codefathers.cfkserver.model.entities.logs.PurchaseLog;
import com.codefathers.cfkserver.model.entities.maps.DiscountcodeIntegerMap;
import com.codefathers.cfkserver.model.entities.product.Document;
import com.codefathers.cfkserver.model.entities.request.Request;
import lombok.*;
import javax.persistence.*;
import java.util.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "t_customer")
public class Customer extends User {
    private long balance;

    @ElementCollection(targetClass = CustomerInformation.class)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable
    private List<CustomerInformation> customerInformation;

    @ElementCollection(targetClass = PurchaseLog.class)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable
    private List<PurchaseLog> purchaseLogs;

    @ElementCollection
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable
    private List<DiscountcodeIntegerMap> discountCodes;

    @ElementCollection
    @OneToMany
    private List<Request> requests;

    @ElementCollection
    @ManyToMany(cascade = CascadeType.ALL)
    private List<Document> documentsPurchased;

    private long allPurchase;

    public Customer(String username, String password, String firstName, String lastName, String email, String phoneNumber, Cart cart, long balance, String accountId) {
        super(username, password, firstName, lastName, email, phoneNumber, cart, accountId);
        this.balance = balance;
        this.customerInformation = new ArrayList<>();
        this.purchaseLogs = new ArrayList<>();
        this.discountCodes = new ArrayList<>();
        this.requests = new ArrayList<>();
        this.documentsPurchased = new ArrayList<>();
    }

    public Customer(){
        this.customerInformation = new ArrayList<>();
        this.purchaseLogs = new ArrayList<>();
        this.discountCodes = new ArrayList<>();
        this.requests = new ArrayList<>();
        this.documentsPurchased = new ArrayList<>();
    }

    public void addRequest(Request request) {
        requests.add(request);
    }
}