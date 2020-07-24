package com.codefathers.cfkserver.model.dtos.log;

import com.codefathers.cfkserver.model.entities.logs.DeliveryStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseLogDTO {
    private int id;
    private String date;
    private long price;
    private DeliveryStatus status;
}
