package com.codefathers.cfkserver.model.entities.maps;

import com.codefathers.cfkserver.model.entities.offs.DiscountCode;
import lombok.*;

import javax.persistence.*;

@Data
@Entity
@Table(name = "t_discount_int_map")
public class DiscountcodeIntegerMap {
    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue
    private int id;

    @OneToOne
    @JoinColumn(name = "CODE")
    private DiscountCode discountCode;

    @Column(name = "INTEGER_VALUE")
    int integer;
}