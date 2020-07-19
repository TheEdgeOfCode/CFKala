package com.codefathers.cfkclient.dtos.off;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class CreateOffDTO {
    private Date start;
    private Date end;
    private int percentage;
}
