package com.codefathers.cfkclient.dtos.off;

import com.codefathers.cfkclient.dtos.product.MiniProductDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class OffDTO {
    private int offId;
    private ArrayList<MiniProductDto> products;
    private String seller;
    private Date startTime;
    private Date endTime;
    private int offPercentage;
    private String status;

    @Override
    public String toString() {
        return String.valueOf(offId);
    }
}
