package com.codefathers.cfkclient.dtos.edit;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class TollMinimumBalanceEditAttribute extends EditAttributes{
    private String newToll;
    private String newMinimumBalance;
}
