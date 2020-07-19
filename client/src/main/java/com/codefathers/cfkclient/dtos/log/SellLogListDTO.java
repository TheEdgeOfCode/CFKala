package com.codefathers.cfkclient.dtos.log;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class SellLogListDTO {
    private List<SellLogDTO> sellLogDTOList;
}
