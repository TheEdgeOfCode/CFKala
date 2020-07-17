package com.codefathers.cfkserver.model.dtos.content;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateAd {
    private int id;
    private String username;
}
