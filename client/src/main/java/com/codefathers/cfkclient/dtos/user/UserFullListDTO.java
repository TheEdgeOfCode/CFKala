package com.codefathers.cfkclient.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data @AllArgsConstructor
@NoArgsConstructor

public class UserFullListDTO {
    List<UserFullDTO> dtos;
}
