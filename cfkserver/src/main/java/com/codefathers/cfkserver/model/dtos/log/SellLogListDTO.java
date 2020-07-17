package com.codefathers.cfkserver.model.dtos.log;

import com.codefathers.cfkserver.model.entities.logs.SellLog;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SellLogListDTO {
    private List<SellLogDTO> sellLogDTOList;
}
