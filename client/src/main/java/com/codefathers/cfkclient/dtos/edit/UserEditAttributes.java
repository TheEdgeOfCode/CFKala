package com.codefathers.cfkclient.dtos.edit;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor

public class UserEditAttributes extends EditAttributes {
    String newPassword;
    String newFirstName;
    String newLastName;
    String newEmail;
    String newPhone;
}