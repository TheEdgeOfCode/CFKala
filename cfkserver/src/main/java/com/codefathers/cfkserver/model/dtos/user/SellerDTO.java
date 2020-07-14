package com.codefathers.cfkserver.model.dtos.user;

import lombok.Data;

@Data
public class SellerDTO extends UserDTO {
    private CompanyDto company;
    private long balance;
}
