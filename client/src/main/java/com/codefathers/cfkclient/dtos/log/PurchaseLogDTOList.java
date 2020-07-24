package com.codefathers.cfkclient.dtos.log;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PurchaseLogDTOList {
    private List<PurchaseLogDTO> dtos;
}
