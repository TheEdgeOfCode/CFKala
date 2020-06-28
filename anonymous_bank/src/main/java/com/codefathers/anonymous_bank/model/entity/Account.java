package com.codefathers.anonymous_bank.model.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table
public class Account {
    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    String username;
    String firstName;
    String lastName;
    String password;
    long balance;
}
