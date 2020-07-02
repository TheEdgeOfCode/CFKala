package com.codefathers.cfkserver.model.entities.request;

import com.codefathers.cfkserver.model.entities.contents.Advertise;
import com.codefathers.cfkserver.model.entities.offs.Off;
import com.codefathers.cfkserver.model.entities.product.Comment;
import com.codefathers.cfkserver.model.entities.product.Product;
import com.codefathers.cfkserver.model.entities.request.edit.OffChangeAttributes;
import com.codefathers.cfkserver.model.entities.request.edit.ProductEditAttribute;
import com.codefathers.cfkserver.model.entities.user.Seller;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "t_request")
public class Request {
    @Id
    @GeneratedValue
    private int requestId;
    private String userHasRequested;

    @Enumerated(EnumType.STRING)
    RequestType requestType;

    @Column(length = 4096)
    String request;

    @OneToOne
    @JoinColumn
    Off off;

    @OneToOne
    @JoinColumn
    OffChangeAttributes offEdit;

    @OneToOne
    @JoinColumn
    Product product;

    @OneToOne
    @JoinColumn
    ProductEditAttribute productEditAttribute;

    @OneToOne
    @JoinColumn
    Comment comment;

    @OneToOne
    Advertise advertise;

    @ManyToOne
    Seller seller;

    private boolean done;

    private boolean accepted;

    public Request(String usernameHasRequested, RequestType requestType, String request,Object toChange) {
        this.userHasRequested = usernameHasRequested;
        this.requestType = requestType;
        this.request = request;
        done = false;
        accepted = false;
        String className = toChange.getClass().getName();

        switch (className) {
            case "ModelPackage.System.editPackage.OffChangeAttributes":
                offEdit = (OffChangeAttributes) toChange;
                break;
            case "ModelPackage.Off.Off" :
                off = (Off) toChange;
                break;
            case "ModelPackage.Product.Comment":
                comment = (Comment) toChange;
                break;
            case "ModelPackage.Product.Product":
                product = (Product) toChange;
                break;
            case "ModelPackage.Users.Seller":
                seller = (Seller) toChange;
                break;
            case "ModelPackage.System.editPackage.ProductEditAttribute":
                productEditAttribute = (ProductEditAttribute) toChange;
                break;
            case "ModelPackage.Users.Advertise":
                advertise = (Advertise) toChange;
                break;
        }
    }

    public Request() {

    }
}