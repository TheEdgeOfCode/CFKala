package com.codefathers.anonymous_bank.model.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table
@RequiredArgsConstructor
@NoArgsConstructor
public class Account {
    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    @NonNull
    String username;
    @NonNull
    String firstName;
    @NonNull
    String lastName;
    @NonNull
    String password;
    long balance;
}
