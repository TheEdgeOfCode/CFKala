package com.codefathers.cfkclient.dtos.log;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseLogDTO {
    private int id;
    private String date;
    private long price;
    private DeliveryStatus status;
}
