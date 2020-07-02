package com.codefathers.cfkserver.model.entities.request.edit;

import lombok.*;
import javax.persistence.*;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class OffChangeAttributes extends EditAttributes {
    @Temporal(TemporalType.DATE)
    Date start;
    @Temporal(TemporalType.DATE)
    Date end;
    int percentage;
    int productIdToRemove;
    int productIdToAdd;
}