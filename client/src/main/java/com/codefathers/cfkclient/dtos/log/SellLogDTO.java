package com.codefathers.cfkclient.dtos.log;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class SellLogDTO {
    private int id;
    private int productId;
    private int moneyGotten;
    private int discount;
    private Date date;
    private String buyer;
    private DeliveryStatus deliveryStatus;
}
