package com.codefathers.cfkserver.model.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestsListDTO {
    private ArrayList<RequestDTO> dtos;
}
