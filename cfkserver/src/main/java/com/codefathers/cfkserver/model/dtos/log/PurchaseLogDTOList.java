package com.codefathers.cfkserver.model.dtos.log;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PurchaseLogDTOList {
    private List<PurchaseLogDTO> dtos;
}
