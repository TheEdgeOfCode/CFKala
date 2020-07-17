package com.codefathers.cfkclient.controllers;

import com.codefathers.cfkclient.CFK;
import com.codefathers.cfkclient.dtos.customer.CartDTO;
import com.codefathers.cfkclient.dtos.customer.PurchaseDTO;
import com.codefathers.cfkclient.dtos.discount.DisCodeUserDTO;
import com.codefathers.cfkclient.utils.Connector;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Purchase {
    @FXML private JFXButton back;
    @FXML private JFXButton minimize;
    @FXML private JFXButton close;
    @FXML private TextField card1;
    @FXML private TextField card2;
    @FXML private TextField card3;
    @FXML private TextField card4;
    @FXML private TextField cvv2;
    @FXML private TextField pass;
    @FXML private TextField month;
    @FXML private TextField year;
    @FXML private Button purchaseButt;
    @FXML
    private AnchorPane rootPane;
    @FXML
    private Label totalPrice;
    @FXML
    private ComboBox<String> country;
    @FXML
    private ComboBox<String> city;
    @FXML
    private JFXTextArea zipCode;
    @FXML
    private JFXTextArea address;
    @FXML
    private JFXTabPane tabPane;
    @FXML
    private Button nextPagePostalInformation;
    @FXML
    private Button nextPageDiscount;
    @FXML
    private Button previousPageDiscount;
    @FXML
    private JFXTextField discountField;
    @FXML
    private JFXButton useDiscount;

    private static final String CARD_REGEX = "\\d{4}";
    private static final String CVV2_REGEX = "\\d{3}\\d?";
    private static final String MONTH_REGEX = "(0[1-9]|1[012])";
    private static final String YEAR_REGEX = "\\d{2}";
    private static final String CARD_PASS_REGEX = "\\d{5,64}";
    private static final Paint redColor = Paint.valueOf("#c0392b");

    private CartDTO cartDTO;
    private final Connector connector;
    private List<DisCodeUserDTO> discountCodes;
    private String selectedDisCode = "";
    private boolean usedDisCodeThisTime = false;

    public Purchase(Connector connector) {
        this.connector = connector;
    }

    // TODO : send notification

    @FXML
    public void initialize() {
        initButtons();
        bindings();
        loadComboBoxes();
        loadCart();
        loadDiscountCode();
        initializeLabels();
    }

    private void loadDiscountCode() {
        try {
            discountCodes = connector.showDiscountCodes();
        } catch (Exception ignore) {}
    }

    private void loadCart() {
        try {
            cartDTO = connector.showCart();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeLabels() {
        totalPrice.setText(cartDTO.getTotalPrice() + "$");
    }

    private void loadComboBoxes() {
        loadCountryComboBox();
        loadCityComboBox();
    }

    private void loadCityComboBox() {
        country.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null) {
                city.getItems().clear();
                city.setDisable(true);
            } else {
                List<String> cities = getCitiesForCountry(newValue);
                city.getItems().setAll(cities);
                city.setDisable(false);
            }
        });
    }

    private List<String> getCitiesForCountry(String country) {
        ArrayList<String> cities = new ArrayList<>();
        switch (country) {
            case "USA" : {
                cities.add("New York");
                cities.add("Washington DC");
                cities.add("Las Vegas");
                cities.add("Los Angeles");
                break;
            }
            case "UK" : {
                cities.add("London");
                cities.add("Manchester");
                cities.add("New Castle");
                cities.add("Cambridge");
                break;
            }
            case "Iran" : {
                cities.add("Tehran");
                cities.add("Yazd");
                cities.add("Shiraz");
                cities.add("Mashhad");
                break;
            }
            case "Canada" : {
                cities.add("Toronto");
                cities.add("Vancouver");
                cities.add("Ottawa");
                cities.add("Montreal");
                break;
            }
        }
        return cities;
    }

    private void loadCountryComboBox() {
        ArrayList<String> count = new ArrayList<>();
        count.add("USA");
        count.add("Canada");
        count.add("Iran");
        count.add("UK");
        ObservableList<String> countries = FXCollections.observableArrayList(count);
        country.setItems(countries);
    }

    private void bindings() {
        nextPagePostalInformation.disableProperty().bind(
                Bindings.isNull(country.getSelectionModel().selectedItemProperty())
                        .or(Bindings.isNull(city.getSelectionModel().selectedItemProperty()))
                        .or(Bindings.isEmpty(address.textProperty()))
                        .or(Bindings.isEmpty(zipCode.textProperty()))
        );
    }

    private void initButtons() {
        back.setOnAction(e -> handleBackButton());
        minimize.setOnAction(e -> minimize());
        close.setOnAction(e -> close());
        nextPagePostalInformation.setOnAction(e -> tabPane.getSelectionModel().selectNext());
        nextPageDiscount.setOnAction(e -> tabPane.getSelectionModel().selectNext());
        previousPageDiscount.setOnAction(e -> tabPane.getSelectionModel().selectPrevious());
        useDiscount.setOnAction(e -> handleUseDiscountButt());
        purchaseButt.setOnAction(e -> handlePurchaseButt());
    }

    private void handleUseDiscountButt() {
        String disCodeId = discountField.getText();
        if (disCodeId.isEmpty())
            errorField(discountField, "Discount Code Is Required!!");
        if (!usedDisCodeThisTime && isValidDisCode(disCodeId)) {
            usedDisCodeThisTime = true;
            selectedDisCode = disCodeId;
            updateTotalPrice();
        }
    }

    private void updateTotalPrice() {
        try {
            totalPrice.setText(String.valueOf(connector.showPurchaseTotalPrice(selectedDisCode)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isValidDisCode(String disCodeId) {
        for (DisCodeUserDTO discountCode : discountCodes) {
            if (discountCode.getDiscountCode().equals(disCodeId))
                return true;
        }
        errorField(discountField, "Invalid Discount Code!!");
        return false;
    }

    private void errorField(JFXTextField field, String message) {
        field.setFocusColor(redColor);
        field.setPromptText(message);
        field.requestFocus();
    }

    private void errorField(JFXTextArea area, String message) {
        area.setFocusColor(redColor);
        area.setPromptText(message);
        area.requestFocus();
    }

    private void handlePurchaseButt() {
        if (checkIfCountryAndCityAreSelected() && checkIfAddressAndZipCodeIsNotEmpty() &&
                checkCardsNumbers() && checkCVV2Numbers() && checkCardPass() && checkExpDateNumber()) {
            try {
                connector.purchase(makePurchaseDTO());
                // TODO : play sound
                reset();
                // TODO : change scene
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private PurchaseDTO makePurchaseDTO() {
        String address_DTO = address.getText();
        String zipCode_DTO = zipCode.getText();
        String cardNumber_DTO = card1.getText() + "-" + card2.getText() +
                "-" + card3.getText() + "-" + card4.getText();
        String cardPass_DTO = pass.getText();
        return new PurchaseDTO(
            address_DTO,
            zipCode_DTO,
            cardNumber_DTO,
            cardPass_DTO,
            selectedDisCode
        );
    }

    private boolean checkIfCountryAndCityAreSelected() {
        if (country.getSelectionModel().getSelectedItem() == null) {
            country.setPromptText("Select A Country!!");
            country.requestFocus();
            return false;
        } else if (city.getSelectionModel().getSelectedItem() == null) {
            city.setPromptText("Select A City!!");
            city.requestFocus();
            return false;
        }
        return true;
    }

    private boolean checkIfAddressAndZipCodeIsNotEmpty() {
        if (address.getText().isEmpty()) {
            errorField(address, "Address Is Required!!");
            return false;
        } else if (zipCode.getText().isEmpty()) {
            errorField(zipCode, "Zip Code Is Required!!");
            return false;
        }
        return true;
    }

    private boolean checkCardsNumbers() {
        if (card1.getText().isEmpty() || card2.getText().isEmpty() ||
                card3.getText().isEmpty() || card4.getText().isEmpty()) {
            // notification : "Fill The Field, Please!"
            return false;
        } else if (!card1.getText().matches(CARD_REGEX) || !card2.getText().matches(CARD_REGEX) ||
                !card3.getText().matches(CARD_REGEX) || !card4.getText().matches(CARD_REGEX)) {
            // notification : "Invalid Card Number!"
            return false;
        }
        return true;
    }

    private boolean checkCVV2Numbers() {
        if (cvv2.getText().isEmpty()) {
            // notification : "Fill CVV2 field, please!"
            return false;
        }
        if (!cvv2.getText().matches(CVV2_REGEX)) {
            // notification : "Invalid CVV2!"
            return false;
        }
        return true;
    }

    private boolean checkCardPass() {
        if (pass.getText().isEmpty()) {
            // notification : "Enter Your Card Password, please!!"
            return false;
        } else if (!pass.getText().matches(CARD_PASS_REGEX)) {
            // notification : "Invalid Card Password!!"
            return false;
        }
        return true;
    }

    private boolean checkExpDateNumber() {
        if (year.getText().isEmpty() || month.getText().isEmpty()) {
            // notification : "Fill Exp. Date, please!"
            return false;
        } else if(!year.getText().matches(YEAR_REGEX) || !month.getText().matches(MONTH_REGEX)) {
            // notification : "Invalid Exp. Date!"
            return false;
        }
        return true;
    }

    private void close() {
        Stage window = (Stage) rootPane.getScene().getWindow();
        window.close();
    }

    private void minimize() {
        Stage window = (Stage) rootPane.getScene().getWindow();
        window.setIconified(true);
    }

    // TODO : check root!!!
    private void handleBackButton() {
        try {
            CFK.setRoot("CustomerAccount");
        } catch (IOException ignore){}
    }

    private void reset() {
        usedDisCodeThisTime = false;
        selectedDisCode = "";
    }
}
