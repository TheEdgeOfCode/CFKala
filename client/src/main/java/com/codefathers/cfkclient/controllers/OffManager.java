package com.codefathers.cfkclient.controllers;

import com.codefathers.cfkclient.CacheData;
import com.codefathers.cfkclient.dtos.off.OffDTO;
import com.codefathers.cfkclient.dtos.product.MiniProductDto;
import com.codefathers.cfkclient.utils.Connector;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;

public class OffManager {
    @FXML
    private JFXButton back;
    @FXML private JFXButton minimize;
    @FXML private JFXButton close;
    @FXML private JFXButton create;

    @FXML private JFXSlider createPercentage;
    @FXML private DatePicker crStartDt;
    @FXML private DatePicker crEndDate;

    @FXML private JFXSlider percent;
    @FXML private DatePicker startDate;
    @FXML private DatePicker endDate;
    @FXML private Label offStatus;

    @FXML private ListView<MiniProductDto> productsList;
    @FXML private ComboBox<MiniProductDto> availableProducts;
    @FXML private Button deleteProduct;
    @FXML private Button addProduct;

    @FXML private JFXButton confirm;
    @FXML private JFXButton reset;
    @FXML private JFXButton removeOff;

    @FXML private ListView<OffDTO> offsList;

    private Connector connector = Connector.getInstance();
    private CacheData cacheData = CacheData.getInstance();
    private final String username = cacheData.getUsername();

    @FXML
    public void initialize() {
        initButtons();
        initOffList();
    }

    private void initButtons() {
        minimize.setOnAction(e -> {
            Stage stage = (Stage) minimize.getScene().getWindow();
            stage.setIconified(true);
        });
        close.setOnAction(e -> {
            Stage stage = (Stage) close.getScene().getWindow();
            stage.close();
        });
        back.setOnAction(event -> handleBack());

        removeOff.setOnAction(event -> handleRemoveOff());
        removeOff.disableProperty().bind(Bindings.isEmpty(offsList.getSelectionModel().getSelectedItems()));

        confirm.setOnAction(event -> handleConfirm());
        confirm.setDisable(true);

        reset.setOnAction(event -> handleReset());
        reset.setDisable(true);

        deleteProduct.setOnAction(event -> handleDeleteProduct());
        deleteProduct.disableProperty().bind(Bindings.isEmpty(productsList.getSelectionModel().getSelectedItems()));

        addProduct.setOnAction(event -> handleAddProduct());
        addProduct.disableProperty().bind(availableProducts.valueProperty().isNull());

        create.setOnAction(event -> handleCreate());
    }

    private void handleCreate() {
        if (crStartDt.getValue().isAfter(crEndDate.getValue()) || crStartDt.getValue().isEqual(crEndDate.getValue())) {
            Notification.show("Error", "Please Check the Dates!!!", back.getScene().getWindow(), true);
        } else {
            try {
                connector.addOff(
                        convertToDateViaInstant(crStartDt.getValue()),
                        convertToDateViaInstant(crEndDate.getValue()),
                        (int) createPercentage.getValue(),
                        username
                );
            } catch (ParseException | InvalidTimes | UserNotAvailableException e) {
                e.printStackTrace();
            }

            resetFields();
            Notification.show("Successful", "Your Request was Sent to The Manager!!!", back.getScene().getWindow(), false);
        }
    }

    private void resetFields() {
        createPercentage.setValue(0);
        crStartDt.getEditor().clear();
        crEndDate.getEditor().clear();
    }

    private java.util.Date convertToDateViaInstant(LocalDate dateToConvert) {
        return java.util.Date.from(dateToConvert.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }

    private void initListeners() {
        percent.valueProperty().addListener(e -> {
            confirm.setDisable(false);
            reset.setDisable(false);
        });
        startDate.valueProperty().addListener(e -> {
            confirm.setDisable(false);
            reset.setDisable(false);
        });
        endDate.valueProperty().addListener(e -> {
            confirm.setDisable(false);
            reset.setDisable(false);
        });
    }

    private void handleBack() {
        try {
            Scene scene = new Scene(Main.loadFXML(back(), backForBackward()));
            Main.setSceneToStage(back, scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initOffList() {
        offsList.getItems().addAll(getOffs());

        //offsList.getItems().addAll(getTestOffs());

        offsList.getSelectionModel().selectedItemProperty().addListener((v, oldOff, newOff) -> changeData(newOff));
    }

    private ObservableList<OffPM> getOffs() {
        ObservableList<OffPM> offs = FXCollections.observableArrayList();

        try {
            offs.addAll(sellerController.viewAllOffs(username, cacheData.getSorts()));
        } catch (UserNotAvailableException e) {
            e.printStackTrace();
        }

        return offs;
    }

    private ObservableList<OffPM> getTestOffs() {
        ObservableList<OffPM> offs = FXCollections.observableArrayList();
        offs.add(new OffPM(
                12456,
                getTestProducts(),
                "marmof",
                new Date(2001, 12, 22),
                new Date(2002, 11, 30),
                50,
                EDIT.toString()
        ));
        offs.add(new OffPM(
                48662,
                getTestProducts(),
                "sapa",
                new Date(2012, 3, 13),
                new Date(2022, 6, 22),
                0,
                CREATION.toString()
        ));
        offs.add(new OffPM(
                21556,
                getTestProducts(),
                "memo",
                new Date(2021, 12, 22),
                new Date(2044, 6, 4),
                2,
                ACCEPTED.toString()
        ));

        return offs;
    }

    private ArrayList<MiniProductPM> getTestProducts() {
        ArrayList<MiniProductPM> products = new ArrayList<>();
        products.add(new MiniProductPM("asus rog g512", 112, "Laptop", "Asus", 5.42, null, null));
        products.add(new MiniProductPM("skirt for kimmi", 245, "Clothes", "Adidas", 5.42, null, null));
        products.add(new MiniProductPM("asus zenbook e333", 230, "Laptop", "Asus", 5.42, null, null));
        products.add(new MiniProductPM("asus vivobook d551", 7885, "Laptop", "Asus", 5.42, null, null));

        return products;
    }

    private void changeData(OffPM off) {
        if (off == null) {
            return;
        }
        offStatus.setText(off.getStatus());
        percent.setValue(off.getOffPercentage());
        startDate.setPromptText(off.getStartTime().toString());
        endDate.setPromptText(off.getEndTime().toString());

        productsList.getItems().clear();
        productsList.getItems().addAll(off.getProducts());

        availableProducts.getItems().clear();
        try {
            availableProducts.getItems().addAll(sellerController.manageProducts(username, cacheData.getSorts()));
        } catch (UserNotAvailableException e) {
            e.printStackTrace();
        }

        initListeners();
        confirm.setDisable(true);
        reset.setDisable(true);
    }

    private void handleRemoveOff() {
        ObservableList<OffPM> offs = offsList.getItems();

        OffPM selected = offsList.getSelectionModel().getSelectedItem();
        offs.remove(selected);

        try {
            sellerController.deleteOff(selected.getOffId(), username);
        } catch (NoSuchAOffException | ThisOffDoesNotBelongssToYouException e) {
            e.printStackTrace();
        }
    }

    private void handleDeleteProduct() {
        ObservableList<MiniProductPM> products = productsList.getItems();


        MiniProductPM selected = productsList.getSelectionModel().getSelectedItem();
        products.remove(selected);

        OffChangeAttributes attributes = new OffChangeAttributes();
        attributes.setProductIdToRemove(selected.getId());
        OffPM off = offsList.getSelectionModel().getSelectedItem();
        attributes.setSourceId(off.getOffId());
        try {
            sellerController.editOff(username, attributes);
        } catch (ThisOffDoesNotBelongssToYouException | NoSuchAOffException e) {
            e.printStackTrace();
        }
    }

    private void handleAddProduct() {
        MiniProductPM selected = availableProducts.getSelectionModel().getSelectedItem();
        OffChangeAttributes attributes = new OffChangeAttributes();
        attributes.setProductIdToAdd(selected.getId());
        attributes.setSourceId(offsList.getSelectionModel().getSelectedItem().getOffId());
        try {
            sellerController.editOff(username, attributes);
            Notification.show("Successful", "Your Request was Sent to The Manager!!!", back.getScene().getWindow(), false);
        } catch (ThisOffDoesNotBelongssToYouException | NoSuchAOffException e) {
            Notification.show("Error", e.getMessage(), back.getScene().getWindow(), true);
            e.printStackTrace();
        }
    }

    private void handleConfirm() {
        OffChangeAttributes attributes = new OffChangeAttributes();
        addAttributes(attributes);

        try {
            sellerController.editOff(username, attributes);
        } catch (ThisOffDoesNotBelongssToYouException | NoSuchAOffException e) {
            e.printStackTrace();
        }

        confirm.setDisable(true);
        reset.setDisable(true);

        Notification.show("Successful", "Your Request was Sent to The Manager!!!", back.getScene().getWindow(), false);
    }

    private void addAttributes(OffChangeAttributes attributes) {
        OffPM off = offsList.getSelectionModel().getSelectedItem();
        attributes.setSourceId(off.getOffId());

        if ((int) percent.getValue() != off.getOffPercentage()) {
            attributes.setPercentage((int) percent.getValue());
        } else if (startDate.getValue() != null) {
            attributes.setStart(convertToDateViaInstant(startDate.getValue()));
        } else if (endDate.getValue() != null) {
            attributes.setEnd(convertToDateViaInstant(endDate.getValue()));
        }
    }

    private void handleReset() {
        OffDTO off = offsList.getSelectionModel().getSelectedItem();

        percent.setValue(off.getOffPercentage());
        startDate.setValue(null);
        startDate.getEditor().clear();
        endDate.setValue(null);
        endDate.getEditor().clear();

        confirm.setDisable(true);
        reset.setDisable(true);
    }
}
