package com.codefathers.cfkserver.model.dtos.edit;

import com.codefathers.cfkserver.model.entities.request.edit.EditAttributes;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class TollMinimumBalanceEditAttribute extends EditAttributes {
    private String newToll;
    private String newMinimumBalance;
}
