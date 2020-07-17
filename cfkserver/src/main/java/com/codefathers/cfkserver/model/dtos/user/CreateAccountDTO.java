package com.codefathers.cfkserver.model.dtos.user;

import com.codefathers.cfkserver.model.entities.user.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateAccountDTO<T> {
    private Role role;
    private T info;
}
