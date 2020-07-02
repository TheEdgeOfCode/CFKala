package com.codefathers.cfkserver.model.entities.logs;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data @NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Log {
    @Id @GeneratedValue
    private int logId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus deliveryStatus;

    public Log(Date date, DeliveryStatus deliveryStatus) {
        this.date = date;
        this.deliveryStatus = deliveryStatus;
    }
}
