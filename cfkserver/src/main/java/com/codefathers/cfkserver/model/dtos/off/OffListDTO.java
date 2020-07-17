package com.codefathers.cfkserver.model.dtos.off;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;

@Data
@AllArgsConstructor
public class OffListDTO {
    private ArrayList<OffDTO> offs;
}
