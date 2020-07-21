package com.codefathers.cfkclient.controllers;

import com.codefathers.cfkclient.BackAbleController;
import com.codefathers.cfkclient.CFK;
import com.codefathers.cfkclient.CacheData;
import com.codefathers.cfkclient.dtos.bank.InfoDTO;
import com.codefathers.cfkclient.dtos.bank.NeededForTransactionDTO;
import com.codefathers.cfkclient.dtos.bank.TransactType;
import com.codefathers.cfkclient.dtos.bank.TransactionDTO;
import com.codefathers.cfkclient.dtos.edit.TollMinimumBalanceEditAttribute;
import com.codefathers.cfkclient.dtos.edit.UserEditAttributes;
import com.codefathers.cfkclient.dtos.user.UserFullDTO;
import com.codefathers.cfkclient.utils.Connector;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.script.Bindings;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static com.codefathers.cfkclient.dtos.bank.TransactType.*;

public class ManagerAccount extends BackAbleController {
    @FXML private TableColumn<TransactionDTO, Integer> idCol;
    @FXML private TableColumn<TransactionDTO, Boolean> paidCol;
    @FXML private TableColumn<TransactionDTO, Integer> destCol;
    @FXML private TableColumn<TransactionDTO, Integer> sourceCol;
    @FXML private TableColumn<TransactionDTO, Long> moneyCol;
    @FXML private TableColumn<TransactionDTO, String> typeCol;
    @FXML private TableView<TransactionDTO> transTable;
    @FXML private JFXComboBox<String> transType;
    @FXML private JFXButton find;
    @FXML private JFXButton confirmButtBud;
    @FXML private JFXButton cancelButtBud;
    @FXML private JFXButton editMinimumButt;
    @FXML private JFXTextField minimumBalanceText;
    @FXML private Label minimumBalance;
    @FXML private JFXButton editTollButt;
    @FXML private Label toll;
    @FXML private JFXTextField tollText;
    @FXML private Label shopBalance;
    @FXML private JFXTextField shopBalanceText;
    @FXML private AnchorPane budget;
    @FXML private AnchorPane main;
    @FXML private JFXButton manageBudget;
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
    private UserFullDTO userFullPM;

    @FXML
    public void initialize() {
        setLabels();
        handleButtons();
        loadImage();
        loadComboBox();
    }

    private void loadComboBox() {
        transType.getItems().add("All");
        transType.getItems().add("Source_You");
        transType.getItems().add("Destination_You");
    }

    private void loadImage() {
        Image image;
        try {
            image = connector.userImage();
            imageCircle.setFill(new ImagePattern(image));
        } catch (Exception e) {
            image = new Image(userPhoto);
            imageCircle.setFill(new ImagePattern(image));
        }
    }

    private void setLabels() {
        try {
            userFullPM = connector.viewPersonalInfo();

            username.setText(userFullPM.getUsername());
            fName.setText(userFullPM.getFirstName());
            lName.setText(userFullPM.getLastName());
            email.setText(userFullPM.getEmail());
            phone.setText(userFullPM.getPhoneNumber());
        } catch (Exception e) {
            new OopsAlert().show(e.getMessage());
            e.printStackTrace();
        }
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
        manageBudget.setOnAction(event -> handleBudgetButt());
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
        editTollButt.setOnAction(event -> handleEditTollButt());
        editMinimumButt.setOnAction(event -> handleEditMinimumButt());
        confirmButt.setOnAction(event -> handleConfirm_Budget());
        cancelButtBud.setOnAction(event -> handleCancelButt_Budget());

        find.setOnAction(event -> handleFindButt());
    }

    private void handleCancelButt_Budget() {
        disableEditFields(tollText, toll);
        disableEditFields(minimumBalanceText, minimumBalance);

        resetSettingForFields(tollText, toll.getText());
        resetSettingForFields(minimumBalanceText, minimumBalance.getText());

        cancelButtBud.setVisible(false);
        confirmButtBud.setVisible(false);
    }

    private void handleConfirm_Budget() {
        TollMinimumBalanceEditAttribute attribute = new TollMinimumBalanceEditAttribute();
        if (updateEditAttributes_Budget(attribute)) {
            try {
                connector.editTollMinimumBalanceInfo(attribute);
            } catch (Exception e) {
                e.printStackTrace();
            }
            confirmButtBud.setVisible(false);
            cancelButtBud.setVisible(false);

            Notification.show("Successful", "Your information is updated successfully!",
                    back.getScene().getWindow(), false);

            handleCancelButt_Budget();
        }
    }

    private boolean updateEditAttributes_Budget(TollMinimumBalanceEditAttribute attribute) {
        boolean succesful = false;
        if (tollText.isVisible() && !checkInput(tollText)) {
            if (tollText.getText().matches("\\b([1-9]|[1-9][0-9]|100)\\b")) {
                attribute.setNewToll(tollText.getText());
                toll.setText(tollText.getText());
                succesful = true;
            }
            else {
                errorField(tollText, "Wrong Toll Format!It Should Be Between 1 To 100!");
                return false;
            }
        } if (minimumBalanceText.isVisible() && !checkInput(minimumBalanceText)) {
            if (minimumBalanceText.getText().matches("\\d+") && Integer.parseInt(minimumBalanceText.getText()) > 0) {
                attribute.setNewMinimumBalance(minimumBalanceText.getText());
                minimumBalance.setText(minimumBalanceText.getText());
                succesful = true;
            }
            else {
                errorField(minimumBalanceText, "Balance Should Be Positive Numerical!");
                return false;
            }
        }
        return succesful;
    }

    private void handleFindButt() {
        find.visibleProperty().bind(transType.valueProperty().isNull());
        String chosen = transType.getSelectionModel().getSelectedItem();
        switch (chosen) {
            case "All" :
                getTransactionOfType(ALL);
                break;
            case "Source_You" :
                getTransactionOfType(SOURCE_YOU);
                break;
            case "Destination_You" :
                getTransactionOfType(DEST_YOU);
                break;
            default: Notification.show("Error", "Choose one choice in all transactions please!",
                    back.getScene().getWindow(), true);
        }
    }

    private void getTransactionOfType(TransactType transactType) {
        NeededForTransactionDTO dto = new NeededForTransactionDTO(
                userFullPM.getUsername(),
                userFullPM.getPassword(),
                "",
                transactType
        );
        try {
            List<TransactionDTO> dtos = connector.getTransactions(dto);
            transTable.setItems(null);
            loadTable(dtos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadTable(List<TransactionDTO> transactionDTOS) {
        ObservableList<TransactionDTO> data = FXCollections.observableArrayList(transactionDTOS);
        typeCol.setCellValueFactory(new PropertyValueFactory<>("receiptType"));
        moneyCol.setCellValueFactory(new PropertyValueFactory<>("money"));
        sourceCol.setCellValueFactory(new PropertyValueFactory<>("sourceAccountID"));
        destCol.setCellValueFactory(new PropertyValueFactory<>("destAccountID"));
        paidCol.setCellValueFactory(new PropertyValueFactory<>("paid"));
        idCol.setCellValueFactory(new PropertyValueFactory<>("receiptId"));
        transTable.setItems(data);
    }

    private void handleEditMinimumButt() {
        enableEditFields(minimumBalanceText, minimumBalance);
        enableEditButts_Budget();
    }

    private void enableEditButts_Budget() {
        confirmButtBud.setVisible(true);
        cancelButtBud.setVisible(true);
    }

    private void handleEditTollButt() {
        enableEditFields(tollText, toll);
        enableEditButts_Budget();
    }

    private void handleBudgetButt() {
        main.setVisible(false);
        budget.setVisible(true);
        try {
            InfoDTO infoDTO = connector.getManagerInfoInBank();
            initBudgetLabels(infoDTO);
        } catch (Exception e) {
            Notification.show("Error", e.getMessage(), back.getScene().getWindow(), true);
        }
    }

    private void initBudgetLabels(InfoDTO infoDTO) throws Exception {
        toll.setText(infoDTO.getToll());
        minimumBalance.setText(infoDTO.getMinimumBalance());
        shopBalance.setText(calculateTotalBalance());
        shopBalanceText.setPromptText(calculateTotalBalance());
    }

    private String calculateTotalBalance() throws Exception {
        List<UserFullDTO> dtos = connector.showUsers();
        long totalBalance = 0;
        for (UserFullDTO dto : dtos) {
            totalBalance += dto.getBalance();
        }
        return Long.toString(totalBalance);
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
        connector.logout();
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
        boolean successful = false;
        if (fNameText.isVisible() && !checkInput(fNameText)) {
            attributes.setNewFirstName(fNameText.getText());
            fName.setText(fNameText.getText());
            successful = true;
        } if (lNameText.isVisible() && !checkInput(lNameText)) {
            attributes.setNewLastName(lNameText.getText());
            lName.setText(lNameText.getText());
            successful = true;
        } if (phoneText.isVisible() && !checkInput(phoneText)) {
            if (phoneText.getText().matches("\\d+")) {
                attributes.setNewPhone(phoneText.getText());
                phone.setText(phoneText.getText());
                successful = true;
            } else {
                errorField(phoneText, "Wrong Phone Number Format");
                return false;
            }
        } if (emailText.isVisible() && !checkInput(emailText)) {
            if (emailText.getText().matches(("\\S+@\\S+\\.(org|net|ir|com|uk|site)"))) {
                attributes.setNewEmail(emailText.getText());
                email.setText(emailText.getText());
                return true;
            } else {
                errorField(emailText, "Wrong Email Format");
                return false;
            }
        }
        return successful;
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
