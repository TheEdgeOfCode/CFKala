package com.codefathers.cfkclient.dtos.auction;

import com.codefathers.cfkclient.dtos.product.SellPackageDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuctionDTO {
    private int id;
    private SellPackageDto sellPackageDto;
    private long userPrice;
    private int productId;
    private String productName;
}
