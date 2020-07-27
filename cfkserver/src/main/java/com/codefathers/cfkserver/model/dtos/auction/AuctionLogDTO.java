package com.codefathers.cfkserver.model.dtos.auction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuctionLogDTO {
    private String expression;
    private int auctionId;
    private String username;
    private String price;
}
