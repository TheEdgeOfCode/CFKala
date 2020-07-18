package com.codefathers.cfkclient.dtos.user;


import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ManagerDTO extends UserDTO {
    public ManagerDTO(String username, String password, String firstName, String lastName, String email, String phoneNumber) {
        super(username, password, firstName, lastName, email, phoneNumber);
    }
}
