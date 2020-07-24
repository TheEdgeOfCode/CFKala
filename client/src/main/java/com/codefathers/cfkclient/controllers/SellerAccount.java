package com.codefathers.cfkclient.controllers;

import com.codefathers.cfkclient.*;
import com.codefathers.cfkclient.dtos.bank.*;
import com.codefathers.cfkclient.dtos.edit.UserEditAttributes;
import com.codefathers.cfkclient.dtos.user.ChargeWalletDTO;
import com.codefathers.cfkclient.dtos.user.CompanyDTO;
import com.codefathers.cfkclient.dtos.user.TakeMoneyDTO;
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
import org.controlsfx.control.PopOver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static com.codefathers.cfkclient.dtos.bank.TransactType.*;
import static com.codefathers.cfkclient.dtos.user.Role.SELLER;

public class SellerAccount extends BackAbleController {
    @FXML private Label balance;
    @FXML private JFXButton find;
    @FXML private JFXComboBox<String> transType;
    @FXML private TableColumn<TransactionDTO, Boolean> paidCol;
    @FXML private TableColumn<TransactionDTO, Integer> idCol;
    @FXML private TableColumn<TransactionDTO, Integer> destCol;
    @FXML private TableColumn<TransactionDTO, Integer> sourceCol;
    @FXML private TableColumn<TransactionDTO, Long> moneyCol;
    @FXML private TableColumn<TransactionDTO, String> typeCol;
    @FXML private TableView<TransactionDTO> transTable;
    @FXML private JFXButton takeMoney;
    @FXML private JFXButton chargeWallet;
    @FXML private Label wallet;
    @FXML private AnchorPane budget;
    @FXML private JFXButton auction;
    @FXML private AnchorPane main;
    @FXML private JFXButton manageBud;
    @FXML private JFXButton back;
    @FXML private JFXButton minimize;
    @FXML private JFXButton close;
    @FXML private JFXButton offMenuButt;
    @FXML private JFXButton productsButt;
    @FXML private JFXButton messagesButt;
    @FXML private JFXButton sellHistoryButt;
    @FXML private JFXButton changePassButt;
    @FXML private JFXButton logoutButt;
    @FXML private JFXButton newProduct;
    @FXML private JFXButton editFNameButt;
    @FXML private JFXButton editLNameButt;
    @FXML private JFXButton editEMailButt;
    @FXML private JFXButton editPhoneButt;
    @FXML private Label username;
    @FXML private Label fName;
    @FXML private Label lName;
    @FXML private Label email;
    @FXML private Label phone;
    @FXML private Label companyName;
    @FXML private Label companyPhone;
    @FXML private JFXTextField fNameText;
    @FXML private JFXTextField lNameText;
    @FXML private JFXTextField emailText;
    @FXML private JFXTextField phoneText;
    @FXML private JFXButton cancelButt;
    @FXML private JFXButton confirmButt;
    @FXML private JFXButton chooseProf;
    @FXML private JFXButton ad;
    @FXML private Circle imageCircle;
    @FXML private JFXButton requestsButt;

    private static final Paint redColor = Paint.valueOf("#c0392b");
    private static final Paint blueColor = Paint.valueOf("#405aa8");
    private static final String userPhoto = "/Images/user-png-icon-male-user-icon-512.png";

    private Connector connector = Connector.getInstance();
    private CacheData cacheData = CacheData.getInstance();

    private UserFullDTO userFullDTO;
    private CompanyDTO companyDTO;
    private PopOver popOver;

    public void initialize() {
        setLabels();
        handleButtons();
        loadImage();
        loadComboBox();
    }

    private void loadComboBox() {
        transType.getItems().add("All");
        transType.getItems().add("Dest_You");
        transType.getItems().add("Source_You");
    }

    private void loadImage() {
        Image image = null;
        try {
            image = connector.userImage();
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
            userFullDTO = connector.viewPersonalInfo();
            companyDTO = connector.viewCompanyInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*userFullPM = getTestUser();
        companyPM = gatTestCompany();*/

        username.setText(userFullDTO.getUsername());
        fName.setText(userFullDTO.getFirstName());
        lName.setText(userFullDTO.getLastName());
        email.setText(userFullDTO.getEmail());
        phone.setText(userFullDTO.getPhoneNumber());
        wallet.setText(String.valueOf(userFullDTO.getBalance()));
        try {
            balance.setText(String.valueOf(connector.getBalance(cacheData.getUsername())));
        } catch (Exception e) {
            Notification.show("Error", e.getMessage(), back.getScene().getWindow(), true);
            e.printStackTrace();
        }
        companyName.setText(companyDTO.getName() + "  (ID : " + companyDTO.getId() + " )");
        companyPhone.setText(companyDTO.getPhone());
    }

    private CompanyDTO gatTestCompany() {
        return new CompanyDTO(
                "Apple",
                "Digital",
                1,
                "+5 55 22 333"
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
        offMenuButt.setOnAction(event -> handleOffs());
        productsButt.setOnAction(event -> handleProducts());
        messagesButt.setOnAction(event -> handleMessage());
        sellHistoryButt.setOnAction(event -> handleSellHistory());
        changePassButt.setOnAction(event -> handleChangePass());
        logoutButt.setOnAction(event -> handleLogout());
        editFNameButt.setOnAction(event -> handleEditFName());
        editLNameButt.setOnAction(event -> handleEditLName());
        editEMailButt.setOnAction(event -> handleEditEmail());
        editPhoneButt.setOnAction(event -> handleEditPhone());
        confirmButt.setOnAction(event -> handleConfirm());
        cancelButt.setOnAction(event -> handleCancel());
        newProduct.setOnAction(event -> handleNewProduct());
        chooseProf.setOnAction(event -> handleChooseProf());
        loadPopup();
        ad.setOnAction(event -> showPopUp());
        requestsButt.setOnAction(event -> handleRequestView());
        manageBud.setOnAction(event -> handleManageBudButt());
        chargeWallet.setOnAction(event -> handleChargeWalletButt());
        takeMoney.setOnAction(event -> handleTakeMoney());
        find.setOnAction(event -> handleFindButt());
        auction.setOnAction(event -> handleAuction());
    }

    private void handleAuction() {
        try {
            Scene scene = new Scene(CFK.loadFXML("createAuction", backForForward("SellerAccount")));
            CFK.setSceneToStage(auction, scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleFindButt() {
        find.disableProperty().bind(transType.valueProperty().isNull());
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
                userFullDTO.getUsername(),
                userFullDTO.getPassword(),
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

    private void handleTakeMoney() {
        long money = new TakeMoneyDialog().show(userFullDTO.getBalance());
        long formerMoney = Long.parseLong(wallet.getText());
        TakeMoneyDTO takeMoneyDTO = new TakeMoneyDTO(
                "",
                money,
                SELLER
        );
        try {
            int receiptId = connector.takeMoneyIntoAccount(takeMoneyDTO);
            balance.setText(String.valueOf(userFullDTO.getBalance() + money));
            wallet.setText(String.valueOf(formerMoney - money));
            Notification.show("Successful", "Successfully Token To Account With ReceiptId "
                    + receiptId, back.getScene().getWindow(), false);
        } catch (Exception e) {
            Notification.show("Error", e.getMessage(), back.getScene().getWindow(), true);
        }
    }

    private void handleChargeWalletButt() {
        long money = new ChargeWalletDialog().show();
        long formerMoney = Long.parseLong(wallet.getText());
        try {
            connector.chargeWallet(new ChargeWalletDTO(money, SELLER));
            wallet.setText(String.valueOf(formerMoney + money));
            balance.setText(String.valueOf(userFullDTO.getBalance() - money));
            Notification.show("Successful", "Charged Successfully", back.getScene().getWindow(), false);
        } catch (Exception e) {
            Notification.show("Error", e.getMessage(), back.getScene().getWindow(), true);
            e.printStackTrace();
        }
    }

    private void handleManageBudButt() {
        main.setVisible(false);
        budget.setVisible(true);
    }

    private void handleRequestView() {
        try {
            Scene scene = new Scene(CFK.loadFXML("RequestView", backForForward("SellerAccount")));
            CFK.setSceneToStage(requestsButt, scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showPopUp() {
        if (popOver.isShowing()) {
            popOver.hide();
        } else {
            SoundCenter.play(Sound.POP_UP);
            popOver.show(ad);
        }
    }

    private void loadPopup() {
        try {
            popOver = new PopOver(CFK.loadFXML("AddAdPopUp"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        popOver.setArrowLocation(PopOver.ArrowLocation.BOTTOM_CENTER);
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

    private void handleNewProduct() {
        try {
            Scene scene = new Scene(CFK.loadFXML("newProduct", backForForward("SellerAccount")));
            CFK.setSceneToStage(back, scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleSellHistory() {
        try {
            Scene scene = new Scene(CFK.loadFXML("SaleHistory", backForForward("SellerAccount")));
            CFK.setSceneToStage(back, scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleMessage() {
        try {
            Scene scene = new Scene(CFK.loadFXML("Messages", backForForward("SellerAccount")));
            CFK.setSceneToStage(back, scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleProducts() {
        try {
            Scene scene = new Scene(CFK.loadFXML("productManagePage", backForForward("SellerAccount")));
            CFK.setSceneToStage(back, scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleOffs() {
        try {
            Scene scene = new Scene(CFK.loadFXML("OffManager", backForForward("SellerAccount")));
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
        CacheData.getInstance().logout();
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
        editPassDialog.show();
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
