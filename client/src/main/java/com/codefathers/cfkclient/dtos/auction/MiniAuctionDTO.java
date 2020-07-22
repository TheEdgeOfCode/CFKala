package com.codefathers.cfkclient.dtos.auction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data @AllArgsConstructor @NoArgsConstructor
public class MiniAuctionDTO {
    private String productName;
    private int id;
    private long money;
    private Date startDate;
    private Date endDate;
    private String sellerName;
}
