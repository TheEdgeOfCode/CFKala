package com.codefathers.cfkclient.dtos.edit;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
// TODO: 7/2/2020 move to dtos no need to save
public class UserEditAttributes extends EditAttributes {
    String newPassword;
    String newFirstName;
    String newLastName;
    String newEmail;
    String newPhone;
}