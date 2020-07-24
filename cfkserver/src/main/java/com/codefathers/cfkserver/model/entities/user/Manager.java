package com.codefathers.cfkserver.model.entities.user;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
public class Manager extends User {
    public Manager(String username, String password, String firstName, String lastName, String email, String phoneNumber, Cart cart, String accountId) {
        super(username, password, firstName, lastName, email, phoneNumber, cart, accountId);
    }
}