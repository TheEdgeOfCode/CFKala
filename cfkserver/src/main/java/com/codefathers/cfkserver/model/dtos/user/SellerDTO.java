package com.codefathers.cfkserver.model.dtos.user;

import com.codefathers.cfkserver.model.entities.product.Company;
import lombok.Data;

@Data
public class SellerDTO extends UserDTO {
    private Company company;
    private long balance;
}
