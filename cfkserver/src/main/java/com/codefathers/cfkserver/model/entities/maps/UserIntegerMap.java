package com.codefathers.cfkserver.model.entities.maps;

import com.codefathers.cfkserver.model.entities.offs.DiscountCode;
import com.codefathers.cfkserver.model.entities.user.User;
import lombok.*;
import javax.persistence.*;

@Data
@Entity
public class UserIntegerMap {
    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue
    private int id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn
    private User user;

    int integer;

    @ManyToOne
    private DiscountCode discountCode;
}