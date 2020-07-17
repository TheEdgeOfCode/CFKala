package com.codefathers.cfkclient.dtos.edit;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class OffChangeAttributes extends EditAttributes {
    Date start;
    Date end;
    int percentage;
    int productIdToRemove;
    int productIdToAdd;
}