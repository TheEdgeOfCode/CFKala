package com.codefathers.cfkclient.dtos.customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data @AllArgsConstructor
@NoArgsConstructor
public class InCartArrayListDTO {
    private ArrayList<InCartDTO> inCartDTOS;
}
