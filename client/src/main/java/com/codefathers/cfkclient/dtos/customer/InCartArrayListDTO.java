package com.codefathers.cfkclient.dtos.customer;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;

@Data @AllArgsConstructor
public class InCartArrayListDTO {
    private ArrayList<InCartDTO> inCartDTOS;
}
