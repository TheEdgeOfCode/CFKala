package com.codefathers.cfkserver.model.dtos.off;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class CreateOffDTO {
    private Date start;
    private Date end;
    private int percentage;
}
