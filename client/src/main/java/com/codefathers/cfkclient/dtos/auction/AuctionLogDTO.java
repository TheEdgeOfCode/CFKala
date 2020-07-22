package com.codefathers.cfkclient.dtos.auction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuctionLogDTO {
    private int auctionId;
    private String expression;
    private String username;
    private String price;

    public AuctionLogDTO(String expression, String username, String price) {
        this.expression = expression;
        this.username = username;
        this.price = price;
    }
}
