package com.codefathers.cfkclient.dtos.off;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class OffListDTO {
    private ArrayList<OffDTO> offs;
}
