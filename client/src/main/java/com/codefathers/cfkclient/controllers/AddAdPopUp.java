package com.codefathers.cfkclient.controllers;

import com.codefathers.cfkclient.CacheData;
import com.codefathers.cfkclient.dtos.content.CreateAd;
import com.codefathers.cfkclient.dtos.product.MicroProductDto;
import com.codefathers.cfkclient.utils.Connector;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.controlsfx.control.SearchableComboBox;

public class AddAdPopUp {
    public SearchableComboBox<MicroProductDto> products;
    public Button createAd;

    @FXML
    public void initialize() {
        loadProducts();
        binds();
        createAd.setOnAction(event -> handleAdvertise());
    }

    private void binds() {
        createAd.disableProperty().bind(products.valueProperty().isNull());
    }

    private void loadProducts() {
        try {
            ObservableList<MicroProductDto> data = FXCollections.observableArrayList(
                    Connector.getInstance().sellerMicroProduct(CacheData.getInstance().getUsername())
            );
            products.setItems(data);
        } catch (Exception e) {
            new OopsAlert().show(e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleAdvertise() {
        int id = products.getSelectionModel().getSelectedItem().getId();
        String username = CacheData.getInstance().getUsername();
        try {
            Connector.getInstance().addAd(new CreateAd(id, username));
            Stage stage = (Stage) Stage.getWindows().filtered(Window::isFocused).get(0);
            Notification.show("Successful", "Request Sent To Manager", stage, false);
        } catch (Exception e) {
            Stage stage = (Stage) Stage.getWindows().filtered(Window::isFocused).get(0);
            Notification.show("Error", e.getMessage(), stage, true);
            e.printStackTrace();
        }
    }
}
