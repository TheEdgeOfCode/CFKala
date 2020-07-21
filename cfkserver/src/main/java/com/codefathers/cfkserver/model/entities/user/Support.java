package com.codefathers.cfkserver.model.entities.user;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
public class Support extends User {
    public Support(String username, String password, String firstName, String lastName, String email, String phoneNumber, Cart cart) {
        super(username, password, firstName, lastName, email, phoneNumber, cart);
    }
}