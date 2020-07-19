package com.codefathers.cfkserver.model.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SellerDTO extends UserDTO {
    private int companyID;
    private long balance;
}
