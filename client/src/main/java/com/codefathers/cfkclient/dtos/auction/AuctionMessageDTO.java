package com.codefathers.cfkclient.dtos.auction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuctionMessageDTO {
    private String message;
    private String username;
    private String date;
}
