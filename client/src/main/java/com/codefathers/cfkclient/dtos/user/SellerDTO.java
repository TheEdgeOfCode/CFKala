package com.codefathers.cfkclient.dtos.user;

import com.codefathers.cfkserver.model.entities.product.Company;
import lombok.Data;

@Data
public class SellerDTO extends UserDTO {
    private Company company;
    private long balance;
}
