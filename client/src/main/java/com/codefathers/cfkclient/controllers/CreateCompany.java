package com.codefathers.cfkclient.controllers;

import com.codefathers.cfkclient.CFK;
import com.codefathers.cfkclient.CacheData;
import com.codefathers.cfkclient.utils.Connector;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;

import java.io.IOException;

public class CreateCompany {
    @FXML
    private AnchorPane rootPane;
    @FXML private JFXButton back;
    @FXML private JFXTextField name;
    @FXML private JFXTextField category;
    @FXML private JFXTextField phone;
    @FXML private JFXButton createButt;

    private static final Paint redColor = Paint.valueOf("#c0392b");
    private static final Paint blueColor = Paint.valueOf("#405aa8");

    private static Connector connector = Connector.getInstance();

    @FXML
    public void initialize() {
        buttonInitialize();
        fieldsInitialize();
    }

    private void buttonInitialize() {
        back.setOnAction(e->{
            try {
                CFK.setRoot("sellerSignUp");
            } catch (IOException ignore) {}
        });
        createButt.setOnAction(e -> {
            if (checkForErrors()){
                sendCompanyRequestSend();
                Notification.show("Successful", "Your Company was Created!!!", back.getScene().getWindow(), false);
                try {
                    Scene scene = new Scene(CFK.loadFXML("SellerSignUp"));
                    CFK.setSceneToStage(back, scene);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void sendCompanyRequestSend() {
        String[] info = new String[3];
        info[0] = name.getText();
        info[1] = phone.getText();
        info[2] = category.getText();
        try {
            int id = connector.createCompany(info);
            CacheData.getInstance().setCompanyID(id);
        } catch (Exception e) {
            Notification.show("Error",e.getMessage(),back.getScene().getWindow(),true);
        }
    }

    private boolean checkForErrors() {
        if (name.getText().isEmpty()) {
            errorField(name,"Name Is Required");
            return false;
        } else if (category.getText().isEmpty()) {
            errorField(category,"Company Category Is Required");
            return false;
        } else if (phone.getText().isEmpty()) {
            errorField(phone,"Phone Is Required");
            return false;
        } else return true;
    }

    private void fieldsInitialize() {
        resetSettingForFields(name,"Company Name");
        resetSettingForFields(phone,"Phone");
        resetSettingForFields(category,"Company Category");
    }

    private void errorField(JFXTextField field,String prompt){
        field.setPromptText(prompt);
        field.setFocusColor(redColor);
        field.requestFocus();
    }

    private void resetSettingForFields(JFXTextField field,String prompt){
        field.textProperty().addListener(e->{
            field.setFocusColor(blueColor);
            field.setPromptText(prompt);
        });
    }
}
