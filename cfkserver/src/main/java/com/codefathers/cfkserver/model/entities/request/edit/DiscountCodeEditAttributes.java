package com.codefathers.cfkserver.model.entities.request.edit;

import lombok.*;
import javax.persistence.*;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class DiscountCodeEditAttributes extends EditAttributes{
    @Temporal(TemporalType.DATE)
    private Date start;
    @Temporal(TemporalType.DATE)
    private Date end;
    private int offPercent;
    private int maxDiscount;
}
