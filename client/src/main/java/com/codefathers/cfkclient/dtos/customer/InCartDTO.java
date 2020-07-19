package com.codefathers.cfkclient.dtos.customer;

import com.codefathers.cfkclient.dtos.product.MiniProductDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor
@NoArgsConstructor
public class InCartDTO {
    private MiniProductDto product;
    private String productName;
    private String sellerId;
    private int price;
    private int offPrice;
    private int amount;
    private int total;

    public InCartDTO(MiniProductDto product, String sellerId, int price, int offPrice, int amount) {
        this.product = product;
        productName = product.getName();
        this.sellerId = sellerId;
        this.price = price;
        this.offPrice = offPrice;
        this.amount = amount;
        total = amount * (offPrice == 0 ? price : offPrice);
    }

    public void setAmount(int amount) {
        this.amount = amount;
        total = amount * (offPrice == 0 ? price : offPrice);
    }

}
