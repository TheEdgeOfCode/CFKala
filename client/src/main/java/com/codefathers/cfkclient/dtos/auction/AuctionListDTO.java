package com.codefathers.cfkclient.dtos.auction;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data @AllArgsConstructor
public class AuctionListDTO {
    List<MiniAuctionDTO> dtos;
}
