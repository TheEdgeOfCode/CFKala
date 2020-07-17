package com.codefathers.cfkclient.dtos.edit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiscountCodeEditAttributes extends EditAttributes{
    private Date start;
    private Date end;
    private int offPercent;
    private int maxDiscount;
    private String code;
}
