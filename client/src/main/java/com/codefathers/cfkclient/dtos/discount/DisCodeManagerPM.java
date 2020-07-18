package com.codefathers.cfkclient.dtos.discount;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class DisCodeManagerPM extends DiscountDTO {
    private ArrayList<UserIntegerPM> users;

    public DisCodeManagerPM(String discountCode, Date startTime, Date endTime, int offPercentage, long maxOfPriceDiscounted, ArrayList<UserIntegerPM> users) {
        super(discountCode, startTime, endTime, offPercentage, maxOfPriceDiscounted);
        this.users = users;
    }

    @Override
    public String toString() {
        return super.getDiscountCode();
    }
}
