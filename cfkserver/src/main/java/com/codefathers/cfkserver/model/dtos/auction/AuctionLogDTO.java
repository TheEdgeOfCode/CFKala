package com.codefathers.cfkserver.model.dtos.auction;

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
}
