package com.codefathers.cfkclient.dtos.customer;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data @AllArgsConstructor
public class OrderLogDTO {
    private int orderId;
    private String date;
    private List<OrderProductDTO> orderProductDTOS;
    private String deliveryStatus;
    private long price;
    private long paid;
    private int discount;
}
