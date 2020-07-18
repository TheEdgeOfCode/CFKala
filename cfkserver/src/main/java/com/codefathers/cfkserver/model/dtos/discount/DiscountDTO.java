package com.codefathers.cfkserver.model.dtos.discount;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data @NoArgsConstructor
public abstract class DiscountDTO {
    private String discountCode;
    private Date startTime;
    private Date endTime;
    private int offPercentage;
    private long maxOfPriceDiscounted;

    public DiscountDTO(String discountCode, Date startTime, Date endTime, int offPercentage, long maxOfPriceDiscounted) {
        this.discountCode = discountCode;
        this.startTime = startTime;
        this.endTime = endTime;
        this.offPercentage = offPercentage;
        this.maxOfPriceDiscounted = maxOfPriceDiscounted;
    }
}
