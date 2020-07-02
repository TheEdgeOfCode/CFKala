package com.codefathers.cfkserver.model.entities.request.edit;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
// TODO: 7/2/2020 move to dtos no need to save
public class UserEditAttributes extends EditAttributes {
    String newPassword;
    String newFirstName;
    String newLastName;
    String newEmail;
    String newPhone;
}