package com.codefathers.cfkclient.controllers;

import com.codefathers.cfkclient.CacheData;
import com.codefathers.cfkclient.dtos.product.MicroProductDto;
import com.codefathers.cfkclient.utils.Connector;
import com.jfoenix.controls.JFXButton;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableObjectValue;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;

import javax.naming.Binding;
import java.time.LocalDate;
import java.time.ZoneId;

public class CreateAuction {
    @FXML private ComboBox<MicroProductDto> products;
    @FXML private JFXButton create;
    @FXML private DatePicker endDate;
    @FXML private DatePicker startDate;

    private final Connector connector = Connector.getInstance();
    private final CacheData cacheData = CacheData.getInstance();


    @FXML
    public void initialize() {
        loadProducts();
        initButtons();
    }

    private void initButtons() {
        create.setOnAction(event -> handleCreateButt());
    }

    private void handleCreateButt() {
        create.disableProperty().bind(
                products.valueProperty().isNull()
                .or(startDate.valueProperty().isNull())
                .or(endDate.valueProperty().isNull())
        );
        if (convertToDateViaInstant(startDate.getValue()).after(convertToDateViaInstant(endDate.getValue()))) {
            Notification.show("Error", "Start Date Should Be Before End Date!", create.getScene().getWindow(), true);
        } else {

        }
    }

    private java.util.Date convertToDateViaInstant(LocalDate dateToConvert) {
        return java.util.Date.from(dateToConvert.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }

    private void loadProducts() {
        try {
            products.getItems().addAll(connector.sellerMicroProduct(cacheData.getUsername()));
        } catch (Exception e) {
            Notification.show("Error", e.getMessage(), create.getScene().getWindow(), true);
        }
    }

}
