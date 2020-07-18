package com.codefathers.cfkclient.controllers;

import com.codefathers.cfkclient.BackAbleController;
import com.codefathers.cfkclient.CFK;
import com.codefathers.cfkclient.CacheData;
import com.codefathers.cfkclient.dtos.edit.OffChangeAttributes;
import com.codefathers.cfkclient.dtos.off.CreateOffDTO;
import com.codefathers.cfkclient.dtos.off.OffDTO;
import com.codefathers.cfkclient.dtos.off.OffStatus;
import com.codefathers.cfkclient.dtos.product.MicroProductDto;
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

import static com.codefathers.cfkclient.dtos.off.OffStatus.*;

public class OffManager extends BackAbleController {
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
    @FXML private ComboBox<MicroProductDto> availableProducts;
    @FXML private Button deleteProduct;
    @FXML private Button addProduct;

    @FXML private JFXButton confirm;
    @FXML private JFXButton reset;
    @FXML private JFXButton removeOff;

    @FXML private ListView<OffDTO> offsList;

    private Connector connector = Connector.getInstance();
    private CacheData cacheData = CacheData.getInstance();

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
                        new CreateOffDTO(
                                convertToDateViaInstant(crStartDt.getValue()),
                                convertToDateViaInstant(crEndDate.getValue()),
                                (int) createPercentage.getValue())
                );
            } catch (Exception e) {
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
            Scene scene = new Scene(CFK.loadFXML(back(), backForBackward()));
            CFK.setSceneToStage(back, scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initOffList() {
        offsList.getItems().addAll(getOffs());
        offsList.getSelectionModel().selectedItemProperty().addListener((v, oldOff, newOff) -> changeData(newOff));
    }

    private ObservableList<OffDTO> getOffs() {
        ObservableList<OffDTO> offs = FXCollections.observableArrayList();
        try {
            offs.addAll(connector.viewAllOffs());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return offs;
    }

    private void changeData(OffDTO off) {
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
           availableProducts.getItems().addAll(connector.sellerMicroProduct(cacheData.getUsername()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        initListeners();
        confirm.setDisable(true);
        reset.setDisable(true);
    }

    private void handleRemoveOff() {
        ObservableList<OffDTO> offs = offsList.getItems();

        OffDTO selected = offsList.getSelectionModel().getSelectedItem();
        offs.remove(selected);

        try {
            connector.removeOff(selected.getOffId());
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleDeleteProduct() {
        ObservableList<MiniProductDto> products = productsList.getItems();


        MiniProductDto selected = productsList.getSelectionModel().getSelectedItem();
        products.remove(selected);

        OffChangeAttributes attributes = new OffChangeAttributes();
        attributes.setProductIdToRemove(selected.getId());
        OffDTO off = offsList.getSelectionModel().getSelectedItem();
        attributes.setSourceId(off.getOffId());
        try {
            connector.editOff(attributes);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleAddProduct() {
        MicroProductDto selected = availableProducts.getSelectionModel().getSelectedItem();
        OffChangeAttributes attributes = new OffChangeAttributes();
        attributes.setProductIdToAdd(selected.getId());
        attributes.setSourceId(offsList.getSelectionModel().getSelectedItem().getOffId());
        try {
            connector.editOff(attributes);
            Notification.show("Successful", "Your Request was Sent to The Manager!!!", back.getScene().getWindow(), false);
        } catch (Exception e) {
            Notification.show("Error", e.getMessage(), back.getScene().getWindow(), true);
            e.printStackTrace();
        }
    }

    private void handleConfirm() {
        OffChangeAttributes attributes = new OffChangeAttributes();
        addAttributes(attributes);

        try {
            connector.editOff(attributes);
        } catch (Exception e) {
            e.printStackTrace();
        }

        confirm.setDisable(true);
        reset.setDisable(true);

        Notification.show("Successful", "Your Request was Sent to The Manager!!!", back.getScene().getWindow(), false);
    }

    private void addAttributes(OffChangeAttributes attributes) {
        OffDTO off = offsList.getSelectionModel().getSelectedItem();
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
