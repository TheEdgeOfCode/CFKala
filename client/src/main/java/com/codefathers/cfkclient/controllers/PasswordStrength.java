package com.codefathers.cfkclient.controllers;

import com.jfoenix.controls.JFXPasswordField;
import javafx.scene.paint.Paint;

public class PasswordStrength {
    private static final Paint redColor = Paint.valueOf("#c0392b");
    private static final Paint greenColor = Paint.valueOf("#BBB529");

    public static void bindPassField(JFXPasswordField passwordField) {
        passwordField.textProperty().addListener(e -> {
            if (passwordField.getText().isEmpty()) {
                passwordField.setPromptText("Password is Required");
                passwordField.setFocusColor(redColor);
            }
            int strength = calculatePasswordStrength(passwordField.getText());
            if (strength == 0) {
                passwordField.setPromptText("Password must be more than 8 character");
                passwordField.setFocusColor(redColor);
            } else if (strength < 4) {
                passwordField.setPromptText("Password is weak");
                passwordField.setFocusColor(redColor);
            } else if (strength < 6) {
                passwordField.setPromptText("Password is good");
                passwordField.setFocusColor(greenColor);
            } else if (strength <= 8) {
                passwordField.setPromptText("Password is strong");
                passwordField.setFocusColor(greenColor);
            } else {
                passwordField.setPromptText("Password is INSANE! Hey buddy it's not a FBI account :)");
                passwordField.setFocusColor(greenColor);
            }
        });
    }

    public static int calculatePasswordStrength(String password) {
        int iPasswordScore = 0;

        if (password.length() < 8)
            return 0;
        else if (password.length() >= 10)
            iPasswordScore += 2;
        else
            iPasswordScore += 1;

        if (password.matches("(?=.*[0-9]).*"))
            iPasswordScore += 2;
        if (password.matches("(?=.*[a-z]).*"))
            iPasswordScore += 2;
        if (password.matches("(?=.*[A-Z]).*"))
            iPasswordScore += 2;
        if (password.matches("(?=.*[~!@#$%^&*()_-]).*"))
            iPasswordScore += 2;

        return iPasswordScore;
    }
}
