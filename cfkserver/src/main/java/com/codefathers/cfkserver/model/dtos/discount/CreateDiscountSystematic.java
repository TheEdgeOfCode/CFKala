package com.codefathers.cfkserver.model.dtos.discount;

import com.codefathers.cfkserver.controller.DiscountController;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateDiscountSystematic {
    private String code;
    private DiscountController.SysDis sysDis;
    private int amount;
}
