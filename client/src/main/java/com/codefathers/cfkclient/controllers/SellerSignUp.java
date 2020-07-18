package com.codefathers.cfkclient.controllers;

import com.codefathers.cfkclient.CFK;
import com.codefathers.cfkclient.CacheData;
import com.codefathers.cfkclient.dtos.user.SellerDTO;
import com.codefathers.cfkclient.utils.Connector;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;

import java.io.IOException;

public class SellerSignUp {
    @FXML
    private AnchorPane rootPane;
    @FXML private JFXButton back;
    @FXML private JFXTextField company;
    @FXML private JFXButton signUpSubmit;
    @FXML private JFXButton createCompanyButt;

    private SellerDTO dto;
    private CacheData cacheData = CacheData.getInstance();
    private Connector connector = Connector.getInstance();

    @FXML
    public void initialize() {
        dto = cacheData.getSignUpData();
        int companyId = cacheData.getCompanyID();
        if (companyId != 0) {
            company.setText(Integer.toString(companyId));
        }
        buttonsInitialize();
        fieldInitialize();
        CacheData.getInstance().setCompanyID(0);
    }

    private void fieldInitialize() {
        company.textProperty().addListener(e -> {
            company.setFocusColor(Paint.valueOf("#405aa8"));
            company.setPromptText("Company ID");
        });
    }

    private void buttonsInitialize() {
        back.setOnAction(e -> {
            try {
                Scene scene = new Scene(CFK.loadFXML("MainPage"));
                CFK.setSceneToStage(back, scene);
            } catch (IOException ignore) {
            }
        });
        createCompanyButt.setOnAction(e -> {
            try {
                CFK.setRoot("createCompany");
            } catch (IOException ignore) {
            }
        });
        signUpSubmit.setOnAction(e -> signUpRequest());
    }

    private void signUpRequest() {
        if (company.getText().isEmpty()) {
            company.setPromptText("Company ID Is Required");
            company.setFocusColor(Paint.valueOf("#c0392b"));
            company.requestFocus();
        } else {
            dto.setCompanyID(Integer.parseInt(company.getText()));
            try {
                connector.createSellerAccount(dto);
            } catch (Exception e) {
                Notification.show("Error", e.getMessage(), back.getScene().getWindow(), true);
                e.printStackTrace();
            }
            Notification.show("Successful", "Your Request was Sent to the Manager!!!", back.getScene().getWindow(), false);
            try {
                Scene scene = new Scene(CFK.loadFXML("MainPage"));
                CFK.setSceneToStage(back, scene);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
