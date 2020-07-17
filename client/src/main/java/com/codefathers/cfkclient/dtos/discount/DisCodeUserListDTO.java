package com.codefathers.cfkclient.dtos.discount;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data @AllArgsConstructor
public class DisCodeUserListDTO {
    private List<DisCodeUserDTO> dtos;
}
