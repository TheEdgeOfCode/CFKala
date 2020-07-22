package com.codefathers.cfkserver.model.dtos.auction;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data @AllArgsConstructor
public class MiniAuctionListDTO {
    List<MiniAuctionDTO> dtos;
}
