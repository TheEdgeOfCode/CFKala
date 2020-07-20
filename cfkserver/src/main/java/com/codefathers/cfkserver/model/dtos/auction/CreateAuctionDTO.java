package com.codefathers.cfkserver.model.dtos.auction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateAuctionDTO {
    private int productId;
    private Date start;
    private Date end;
}
