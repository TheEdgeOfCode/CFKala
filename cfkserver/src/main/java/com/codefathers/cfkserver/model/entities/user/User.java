package com.codefathers.cfkserver.model.entities.user;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class User {
    @Id
    @Column(unique = true, length = 64 , nullable = false)
    protected String username;

    @Column(length = 64)
    protected String password;

    protected String firstName;
    protected String lastName;
    protected String email;
    protected String phoneNumber;

    @OneToOne(cascade = CascadeType.ALL)
    protected Cart cart;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages;

    private String accountId;

    public User(String username, String password, String firstName, String lastName, String email, String phoneNumber, Cart cart) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.cart = cart;
        this.messages = new ArrayList<>();
    }
    public User(){
        this.messages = new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return username.equals(user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}