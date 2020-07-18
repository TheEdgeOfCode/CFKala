package com.codefathers.cfkclient.dtos.product;

import lombok.Data;

import java.util.Date;

@Data
public class OffProductPM {
    private String name;
    private int id;
    private int offPrice;
    private int percent;
    private Date end;

    public OffProductPM(String name, int id, int offPrice, int percent, Date end) {
        this.name = name;
        this.id = id;
        this.offPrice = offPrice;
        this.percent = percent;
        this.end = end;
    }
}