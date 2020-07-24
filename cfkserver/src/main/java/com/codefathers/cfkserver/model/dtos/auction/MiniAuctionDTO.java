package com.codefathers.cfkserver.model.dtos.auction;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data @AllArgsConstructor
public class MiniAuctionDTO {
    private AuctionDTO auctionDTO;
    private Date startDate;
    private Date endDate;
}
