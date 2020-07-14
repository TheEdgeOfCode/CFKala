package com.codefathers.cfkserver.model.dtos.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MiniProductListDto {
    ArrayList<MiniProductDto> dtos;
}
