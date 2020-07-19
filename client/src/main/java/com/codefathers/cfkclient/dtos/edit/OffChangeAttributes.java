package com.codefathers.cfkclient.dtos.edit;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor

public class OffChangeAttributes extends EditAttributes {
    Date start;
    Date end;
    int percentage;
    int productIdToRemove;
    int productIdToAdd;
}