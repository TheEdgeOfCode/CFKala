package com.codefathers.cfkclient.dtos.edit;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data@NoArgsConstructor
public abstract class EditAttributes {
    protected int id;
    protected int sourceId;
}