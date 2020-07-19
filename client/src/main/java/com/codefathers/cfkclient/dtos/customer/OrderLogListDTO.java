package com.codefathers.cfkclient.dtos.customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data @AllArgsConstructor
@NoArgsConstructor
public class OrderLogListDTO {
    private List<OrderLogDTO> dtos;
}
