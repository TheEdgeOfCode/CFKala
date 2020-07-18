package com.codefathers.cfkserver.model.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data @AllArgsConstructor
public class UserFullListDTO {
    List<UserFullDTO> dtos;
}
