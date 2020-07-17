package com.codefathers.cfkclient.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDTO {
    private String name;
    private String group;
    private int id;
    private String phone;
}

