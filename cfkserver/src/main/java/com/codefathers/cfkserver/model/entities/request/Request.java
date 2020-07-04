package com.codefathers.cfkserver.model.entities.request;

import com.codefathers.cfkserver.model.entities.contents.Advertise;
import com.codefathers.cfkserver.model.entities.offs.Off;
import com.codefathers.cfkserver.model.entities.product.Comment;
import com.codefathers.cfkserver.model.entities.product.Product;
import com.codefathers.cfkserver.model.entities.request.edit.OffChangeAttributes;
import com.codefathers.cfkserver.model.entities.request.edit.ProductEditAttribute;
import com.codefathers.cfkserver.model.entities.user.Seller;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data @NoArgsConstructor
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

        if (toChange instanceof OffChangeAttributes){
            offEdit = (OffChangeAttributes) toChange;
        }else if (toChange instanceof Off){
            off = (Off) toChange;
        }else if (toChange instanceof Comment){
            comment = (Comment) toChange;
        }else if (toChange instanceof Product){
            product = (Product) toChange;
        }else if (toChange instanceof Seller){
            seller = (Seller) toChange;
        }else if (toChange instanceof ProductEditAttribute){
            productEditAttribute = (ProductEditAttribute) toChange;
        }else if (toChange instanceof Advertise){
            advertise = (Advertise) toChange;
        }
    }
}