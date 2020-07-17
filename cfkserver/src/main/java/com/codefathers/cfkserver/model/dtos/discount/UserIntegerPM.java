package com.codefathers.cfkserver.model.dtos.discount;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserIntegerPM {
    private String username;
    private int integer;
}