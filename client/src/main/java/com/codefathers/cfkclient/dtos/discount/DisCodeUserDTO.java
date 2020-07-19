package com.codefathers.cfkclient.dtos.discount;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor

public class DisCodeUserDTO extends DiscountDTO{
    private int count;

    public DisCodeUserDTO(String discountCode, Date startTime, Date endTime, int offPercentage, long maxOfPriceDiscounted, int count) {
        super(discountCode, startTime, endTime, offPercentage, maxOfPriceDiscounted);
        this.count = count;
    }
}
