package com.codefathers.cfkserver.model.dtos.discount;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateDiscount {
    private String[] data;
    private Date start;
    private Date end;
}
