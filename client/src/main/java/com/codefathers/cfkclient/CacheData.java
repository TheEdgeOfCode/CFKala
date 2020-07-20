package com.codefathers.cfkclient;

import com.codefathers.cfkclient.dtos.auction.AuctionDTO;
import com.codefathers.cfkclient.dtos.user.SellerDTO;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Data;

@Data
public class CacheData {
    private static CacheData cacheData;
    public static CacheData getInstance() { return cacheData; }
    static { cacheData = new CacheData(); }

    public CacheData() {
        roleProperty = new SimpleStringProperty("");
        companyID = 0;
        username = "";
        role = "";
        productId = 2;
    }

    public StringProperty roleProperty;
    private String username;
    private String role;
    private int companyID;
    private int productId;
    private SellerDTO signUpData;

    //-> Auction
    private AuctionDTO auctionDTO;

    public void logout(){
        roleProperty.set("");
        role = "";
        username = "";
    }

    public void setRole(String role) {
        this.role = role;
        roleProperty.setValue(role);
    }
}
