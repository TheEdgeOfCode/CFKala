package com.codefathers.cfkserver.model.dtos.discount;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data @AllArgsConstructor
public class DisCodeUserListDTO {
    private List<DisCodeUserDTO> dtos;
}
