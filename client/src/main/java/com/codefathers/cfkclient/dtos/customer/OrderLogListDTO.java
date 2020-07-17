package com.codefathers.cfkclient.dtos.customer;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data @AllArgsConstructor
public class OrderLogListDTO {
    private List<OrderLogDTO> dtos;
}
