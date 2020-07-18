package com.codefathers.cfkclient.controllers;

import com.codefathers.cfkclient.BackAbleController;
import com.codefathers.cfkclient.CFK;
import com.codefathers.cfkclient.CacheData;
import com.codefathers.cfkclient.dtos.edit.UserEditAttributes;
import com.codefathers.cfkclient.dtos.user.UserFullDTO;
import com.codefathers.cfkclient.utils.Connector;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ManagerAccount extends BackAbleController {
    @FXML
    private JFXButton back;
    @FXML private JFXButton minimize;
    @FXML private JFXButton close;
    @FXML private Circle imageCircle;
    @FXML private JFXButton chooseProf;
    @FXML private JFXButton usersButt;
    @FXML private JFXButton productsButt;
    @FXML private JFXButton categoriesButt;
    @FXML private JFXButton discountButt;
    @FXML private JFXButton requestButt;
    @FXML private JFXButton mainContent;
    @FXML private JFXButton changePassButt;
    @FXML private Label username;
    @FXML private JFXTextField fNameText;
    @FXML private Label fName;
    @FXML private JFXButton editFNameButt;
    @FXML private JFXTextField lNameText;
    @FXML private Label lName;
    @FXML private JFXButton editLNameButt;
    @FXML private JFXTextField emailText;
    @FXML private Label email;
    @FXML private JFXButton editEMailButt;
    @FXML private JFXTextField phoneText;
    @FXML private Label phone;
    @FXML private JFXButton editPhoneButt;
    @FXML private JFXButton cancelButt;
    @FXML private JFXButton confirmButt;
    @FXML private JFXButton addManagerButt;
    @FXML private JFXButton logoutButt;

    private static final Paint redColor = Paint.valueOf("#c0392b");
    private static final Paint blueColor = Paint.valueOf("#405aa8");
    private static final String userPhoto = "/Images/user-png-icon-male-user-icon-512.png";


    private Connector connector = Connector.getInstance();
    private CacheData cacheData = CacheData.getInstance();

    @FXML
    public void initialize() {
        handleButtons();
        setLabels();
        loadImage();
    }

    private void loadImage() {
        Image image;
        try {
            image = connector.userImage(username.getText());
            if (image == null) {
                image = new Image(userPhoto);
            }
            imageCircle.setFill(new ImagePattern(image));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setLabels() {
        try {
            UserFullDTO userFullPM = connector.viewPersonalInfo();

            //UserFullPM userFullPM = getTestUser();

            username.setText(userFullPM.getUsername());
            fName.setText(userFullPM.getFirstName());
            lName.setText(userFullPM.getLastName());
            email.setText(userFullPM.getEmail());
            phone.setText(userFullPM.getPhoneNumber());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private UserFullDTO getTestUser() {
        return new UserFullDTO(
                "marmof",
                "Mohamad",
                "Mofayezi",
                "marmof@gmail.com",
                "989132255442",
                "Manager"
        );
    }

    private void handleButtons() {
        minimize.setOnAction(e -> {
            Stage stage = (Stage) minimize.getScene().getWindow();
            stage.setIconified(true);
        });
        close.setOnAction(e -> {
            Stage stage = (Stage) close.getScene().getWindow();
            stage.close();
        });
        back.setOnAction(event -> handleBack());
        usersButt.setOnAction(event -> handleUsers());
        productsButt.setOnAction(event -> handleProducts());
        categoriesButt.setOnAction(event -> handleCategories());
        discountButt.setOnAction(event -> handleDiscount());
        requestButt.setOnAction(event -> handleRequests());
        changePassButt.setOnAction(event -> handleChangePass());
        logoutButt.setOnAction(event -> handleLogout());
        addManagerButt.setOnAction(event -> handleAddManager());
        chooseProf.setOnAction(event -> handleChooseProf());
        mainContent.setOnAction(event -> handleMainContent());

        editFNameButt.setOnAction(event -> handleEditFName());
        editLNameButt.setOnAction(event -> handleEditLName());
        editEMailButt.setOnAction(event -> handleEditEmail());
        editPhoneButt.setOnAction(event -> handleEditPhone());
        confirmButt.setOnAction(event -> handleConfirm());
        cancelButt.setOnAction(event -> handleCancel());
    }

    private void handleMainContent() {
        try {
            Scene scene = new Scene(CFK.loadFXML("ContentManager", backForForward("ManagerAccount")));
            CFK.setSceneToStage(mainContent, scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleChooseProf() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.jpeg", "*.png", "*.jpg"));
        File selected = fileChooser.showOpenDialog(back.getScene().getWindow());
        if (selected != null) {
            Image toImage = new Image(String.valueOf(selected.toURI()));
            imageCircle.setFill(new ImagePattern(toImage));
            try {
                InputStream inputStream = new FileInputStream(selected);
                connector.saveUserImage(inputStream);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Notification.show("Successful", "Your Profile Pic was Updated!!!", back.getScene().getWindow(), false);
        }
    }

    private void handleAddManager() {
        try {
            Scene scene = new Scene(CFK.loadFXML("CreateManager", backForForward("ManagerAccount")));
            CFK.setSceneToStage(back, scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleBack() {
        try {
            Scene scene = new Scene(CFK.loadFXML(back(), backForBackward()));
            CFK.setSceneToStage(back, scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleUsers() {
        try {
            Scene scene = new Scene(CFK.loadFXML("ManageUsers", backForForward("ManagerAccount")));
            CFK.setSceneToStage(back, scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleProducts() {
        try {
            Scene scene = new Scene(CFK.loadFXML("productManagePage", backForForward("ManagerAccount")));
            CFK.setSceneToStage(back, scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleCategories() {
        try {
            Scene scene = new Scene(CFK.loadFXML("CategoryManager", backForForward("ManagerAccount")));
            CFK.setSceneToStage(back, scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleRequests() {
        try {
            Scene scene = new Scene(CFK.loadFXML("requestManager", backForForward("ManagerAccount")));
            CFK.setSceneToStage(back, scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleDiscount() {
        try {
            Scene scene = new Scene(CFK.loadFXML("DiscountManager", backForForward("ManagerAccount")));
            CFK.setSceneToStage(back, scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleEditPhone() {
        enableEditFields(phoneText, phone);
        enableEditButts();
    }

    private void handleEditEmail() {
        enableEditFields(emailText, email);
        enableEditButts();
    }

    private void handleEditLName() {
        enableEditFields(lNameText, lName);
        enableEditButts();
    }

    private void handleEditFName() {
        enableEditFields(fNameText, fName);
        enableEditButts();
    }

    private void handleLogout() {
        // TODO : logout should be implemented!!!
        cacheData.logout();
        try {
            back.getScene().setRoot(CFK.loadFXML("MainPage"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void enableEditFields(JFXTextField field, Label label) {
        label.setVisible(false);
        field.setVisible(true);
        field.setPromptText(label.getText());
    }

    private void disableEditFields(JFXTextField field, Label label) {
        label.setVisible(true);
        field.setVisible(false);
    }

    private void enableEditButts() {
        confirmButt.setVisible(true);
        cancelButt.setVisible(true);
    }

    private void handleChangePass() {
        EditPassDialog editPassDialog = new EditPassDialog();
        String newPass = editPassDialog.show();

        if (newPass != null) {
            UserEditAttributes attributes = new UserEditAttributes();
            attributes.setNewPassword(newPass);

            try {
                connector.editPersonalInfo(attributes);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Notification.show("Successful", "Your Password was Changed!!!", back.getScene().getWindow(), false);
        }
    }

    private void handleCancel() {
        disableEditFields(phoneText, phone);
        disableEditFields(emailText, email);
        disableEditFields(fNameText, fName);
        disableEditFields(lNameText, lName);

        resetSettingForFields(phoneText, phone.getText());
        resetSettingForFields(emailText, email.getText());
        resetSettingForFields(lNameText, lName.getText());
        resetSettingForFields(fNameText, fName.getText());

        confirmButt.setVisible(false);
        cancelButt.setVisible(false);
    }

    private void handleConfirm() {
        UserEditAttributes attributes = new UserEditAttributes();
        if (updateEditAttributes(attributes)) {
            try {
                connector.editPersonalInfo(attributes);
            } catch (Exception e) {
                e.printStackTrace();
            }

            confirmButt.setVisible(false);
            cancelButt.setVisible(false);

            Notification.show("Successful", "Your Information was Updated!!!", back.getScene().getWindow(), false);
            handleCancel();
        }
    }

    private boolean updateEditAttributes(UserEditAttributes attributes) {
        if (fNameText.isVisible() && !checkInput(fNameText)) {
            attributes.setNewFirstName(fNameText.getText());
            fName.setText(fNameText.getText());
            return true;
        } else if (lNameText.isVisible() && !checkInput(lNameText)) {
            attributes.setNewLastName(lNameText.getText());
            lName.setText(lNameText.getText());
            return true;
        } else if (phoneText.isVisible() && !checkInput(phoneText)) {
            if (phoneText.getText().matches("\\d+")) {
                attributes.setNewPhone(phoneText.getText());
                phone.setText(phoneText.getText());
                return true;
            } else {
                errorField(phoneText, "Wrong Phone Number Format");
                return false;
            }
        } else if (emailText.isVisible() && !checkInput(emailText)) {
            if (emailText.getText().matches(("\\S+@\\S+\\.(org|net|ir|com|uk|site)"))) {
                attributes.setNewEmail(emailText.getText());
                email.setText(emailText.getText());
                return true;
            } else {
                errorField(emailText, "Wrong Email Format");
                return false;
            }
        }
        return false;
    }

    private boolean checkInput(JFXTextField field) {
        return field.getText().isEmpty();
    }

    private void errorField(JFXTextField field, String prompt) {
        field.setPromptText(prompt);
        field.setFocusColor(redColor);
        field.requestFocus();
    }

    private void resetSettingForFields(JFXTextField field, String prompt) {
        field.textProperty().addListener(e -> {
            field.setFocusColor(blueColor);
            field.setPromptText(prompt);
        });
    }
}
