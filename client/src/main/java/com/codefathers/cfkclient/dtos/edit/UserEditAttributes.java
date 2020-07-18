package com.codefathers.cfkclient.dtos.edit;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserEditAttributes extends EditAttributes {
    String newPassword;
    String newFirstName;
    String newLastName;
    String newEmail;
    String newPhone;
}