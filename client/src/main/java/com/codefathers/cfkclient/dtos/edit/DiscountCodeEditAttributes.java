package com.codefathers.cfkclient.dtos.edit;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class DiscountCodeEditAttributes extends EditAttributes{
    private Date start;
    private Date end;
    private int offPercent;
    private int maxDiscount;
}
