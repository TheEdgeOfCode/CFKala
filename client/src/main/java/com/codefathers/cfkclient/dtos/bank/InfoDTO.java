package com.codefathers.cfkclient.dtos.bank;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class InfoDTO {
    private String accountId;
    private String toll;
    private String minimumBalance;
}
