package com.codefathers.cfkclient.dtos.discount;

import com.codefathers.cfkclient.controllers.SysDis;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateDiscountSystematic {
    private String code;
    private SysDis sysDis;
    private int amount;
}
