package com.codefathers.cfkclient.controllers;

import com.codefathers.cfkclient.CFK;
import com.codefathers.cfkclient.dtos.user.*;
import com.codefathers.cfkclient.utils.Connector;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.codefathers.cfkclient.dtos.user.Role.CUSTOMER;
import static com.codefathers.cfkclient.dtos.user.Role.SELLER;

@Component
public class SignInUp {
    @FXML private JFXButton back;
    @FXML private JFXButton signInButton;
    @FXML private JFXButton signUpButton;
    @FXML private Pane signInPage;
    @FXML private Pane signUpPage;
    @FXML private JFXTextField usernameUp;
    @FXML private JFXPasswordField passwordUp;
    @FXML private JFXPasswordField rePasswordUp;
    @FXML private JFXTextField firstName;
    @FXML private JFXTextField lastName;
    @FXML private JFXTextField email;
    @FXML private JFXTextField phone;
    @FXML private JFXTextField balance;
    @FXML private JFXButton sellerBut;
    @FXML private JFXButton signUpSubmit;
    @FXML private JFXButton signInSubmit;
    @FXML private JFXTextField usernameIn;
    @FXML private JFXPasswordField passwordIn;

    private final Connector connector;

    public SignInUp(Connector connector) {
        this.connector = connector;
    }

    private static final Paint redColor = Paint.valueOf("#c0392b");
    private static final Paint blueColor = Paint.valueOf("#405aa8");

    @FXML
    public void initialize() {
        buttonInitialize();
        fieldsInitialize();
    }

    private void fieldsInitialize() {
        signInFields();
        signUpFields();
    }

    private void signInFields() {
        resetSettingForFields(usernameIn, "Username");
        resetSettingForFields(passwordIn, "Password");
    }

    private void signUpFields() {
        resetSettingForFields(usernameUp, "Username");
        resetSettingForFields(firstName, "First Name");
        resetSettingForFields(lastName, "Last Name");
        resetSettingForFields(email, "Email");
        resetSettingForFields(phone, "Phone Number");
        resetSettingForFields(rePasswordUp, "Repeat Password");
        resetSettingForFields(balance, "Balance");
        PasswordStrength.bindPassField(passwordUp);
    }

    private void resetSettingForFields(JFXTextField field, String prompt) {
        field.textProperty().addListener(e -> {
            field.setFocusColor(blueColor);
            field.setPromptText(prompt);
        });
    }

    private void resetSettingForFields(JFXPasswordField field, String prompt) {
        field.textProperty().addListener(e -> {
            field.setFocusColor(blueColor);
            field.setPromptText(prompt);
        });
    }

    private void buttonInitialize() {
        backInitialize();
        inUpSwitch();
        submitButtons();
    }

    private void submitButtons() {
        signInSubmit.setOnAction(e -> signInSubmitRequest());
        signUpSubmit.setOnAction(e -> signUpSubmitRequest());
        sellerBut.setOnAction(e -> sellerSignUpSubmitRequest());
    }

    private void sellerSignUpSubmitRequest() {
        if (checkForEmptyValues()) {
            //TODO: Impl CacheData
            //CacheData.getInstance().setSignUpData(generateInfoPack(SELLER));
            try {
                CFK.setRoot("sellerSignUp");
            } catch (IOException ignore) {
            }
        }
    }

    private void signUpSubmitRequest() {
        if (checkForEmptyValues()) {
            sendSignUpRequest();
            Notification.show("Successful", "Your Account was Created Successfully!!!", back.getScene().getWindow(), false);
            try {
                Scene scene = new Scene(CFK.loadFXML("MainPage"));
                CFK.setSceneToStage(back, scene);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Notification.show("Error", "Please Check The Fields.", back.getScene().getWindow(), true);
        }
    }

    private void sendSignUpRequest() {
        try {
            connector.createCustomerAccount(generateInfoPack(CUSTOMER));
        } catch (Exception e) {
            Notification.show("Error", e.getMessage(), back.getScene().getWindow(), true);
            e.printStackTrace();
        }
    }

    private CreateAccountDTO generateInfoPack(Role role) {
        if (role.equals(CUSTOMER)) {
            return new CreateAccountDTO<>(CUSTOMER, getCustomerDTO());
        } else {
            return new CreateAccountDTO<>(SELLER, getSellerDTO());
        }
    }

    private CustomerDTO getCustomerDTO(){
        return new CustomerDTO(
                usernameUp.getText(),
                passwordUp.getText(),
                firstName.getText(),
                lastName.getText(),
                email.getText(),
                phone.getText(),
                Long.parseLong(balance.getText())
        );
    }

    private SellerDTO getSellerDTO(){
        return new SellerDTO(
                usernameUp.getText(),
                passwordUp.getText(),
                firstName.getText(),
                lastName.getText(),
                email.getText(),
                phone.getText(),
                Long.parseLong(balance.getText())
        );
    }

    private boolean checkForEmptyValues() {
        if (usernameUp.getText().isEmpty()) {
            errorField(usernameUp, "Username Is Required");
            return false;
        } else if (passwordUp.getText().isEmpty()) {
            return false;
        } else if (PasswordStrength.calculatePasswordStrength(passwordUp.getText()) < 4) {
            return false;
        } else if (!rePasswordUp.getText().equals(passwordUp.getText())) {
            errorField(rePasswordUp, "Doesn't Match Above");
            return false;
        } else if (firstName.getText().isEmpty()) {
            errorField(firstName, "First Name Is Required");
            return false;
        } else if (lastName.getText().isEmpty()) {
            errorField(lastName, "Last Name Is Required");
            return false;
        } else if (email.getText().isEmpty()) {
            errorField(email, "Email Is Required");
            return false;
        } else if (!email.getText().matches(("\\S+@\\S+\\.(org|net|ir|com|uk|site)"))) {
            errorField(email, "Wrong Email Format");
            return false;
        } else if (phone.getText().isEmpty()) {
            errorField(phone, "Phone Number Is Required");
            return false;
        } else if (balance.getText().isEmpty()) {
            errorField(balance, "Balance Is Required");
            return false;
        } else if (!balance.getText().matches("(^\\d{1,19}$)?")) {
            errorField(balance, "Balance Must Be Numerical");
            return false;
        } else return true;
    }

    private void errorField(JFXTextField field, String prompt) {
        field.setPromptText(prompt);
        field.setFocusColor(redColor);
        field.requestFocus();
    }

    private void errorField(JFXPasswordField field, String prompt) {
        field.setPromptText(prompt);
        field.setFocusColor(redColor);
        field.requestFocus();
    }

    private void signInSubmitRequest() {
        if (usernameIn.getText().isEmpty()) {
            errorField(usernameIn, "Username Is Required *");
        } else if (passwordIn.getText().isEmpty()) {
            errorField(passwordIn, "Password Is Required");
        } else {
            sendSignInRequest();
            try {
                Scene scene = new Scene(CFK.loadFXML("MainPage"));
                CFK.setSceneToStage(back, scene);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendSignInRequest() {
        //TODO: Handle Exceptions on client side
        try {
           /* CacheData.getInstance().setRole(accountController.login(usernameIn.getText(), passwordIn.getText()));
            CacheData.getInstance().setUsername(usernameIn.getText());*/
            connector.login(new LoginDto(usernameIn.getText(), passwordIn.getText()));
            Notification.show("Successful", "Logged In Successfully!!!", back.getScene().getWindow(), false);
        } /*catch (NotVerifiedSeller e) {
            new OopsAlert().show("Your Account Isn't Verified Yet");
        } catch (UserNotAvailableException e) {
            errorField(usernameIn, "Username Not Exist");
        } catch (WrongPasswordException e) {
            errorField(passwordIn, "Incorrect Password");
        }*/ catch (Exception e) {
            String message = e.getMessage();
            if (message.contains("verified")){
                Notification.show("Error", "Your Account Isn't Verified Yet", back.getScene().getWindow(), true);
                e.printStackTrace();
            } else if (message.contains("exist")){
                Notification.show("Error", "Username Not Exist", back.getScene().getWindow(), true);
                e.printStackTrace();
            } else if (message.contains("wrong")){
                Notification.show("Error", "Incorrect Password", back.getScene().getWindow(), true);
                e.printStackTrace();
            } else {
                //TODO
            }
        }
    }

    private void inUpSwitch() {
        signInButton.setOnAction(e -> {
            if (!signInPage.isVisible()) {
                signUpPage.setVisible(false);
                signInPage.setVisible(true);
            }
        });

        signUpButton.setOnAction(e -> {
            if (!signUpPage.isVisible()) {
                signInPage.setVisible(false);
                signUpPage.setVisible(true);
            }
        });
    }

    private void backInitialize() {
        back.setOnAction(e -> {
            try {
                Scene scene = new Scene(CFK.loadFXML("MainPage"));
                CFK.setSceneToStage(back, scene);
            } catch (IOException ignore) {
            }
        });
    }


}
