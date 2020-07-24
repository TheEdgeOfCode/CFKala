package com.codefathers.cfkserver.model.entities.offs;

import com.codefathers.cfkserver.model.entities.maps.UserIntegerMap;
import lombok.Data;

import javax.persistence.*;
import java.util.*;

@Data
@Entity
public class DiscountCode {
    @Id
    @Column(unique = true, length = 100)
    private String code;

    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;
    @Temporal(TemporalType.TIMESTAMP)
    private Date endTime;
    private int offPercentage;
    private long maxDiscount;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn
    private List<UserIntegerMap> users;

    public DiscountCode(String code,Date startTime, Date endTime, int offPercentage, long maxDiscount) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.offPercentage = offPercentage;
        this.maxDiscount = maxDiscount;
        this.code = code;
        this.users = new ArrayList<>();
    }

    public DiscountCode(){
        this.users = new ArrayList<>();
    }
}
