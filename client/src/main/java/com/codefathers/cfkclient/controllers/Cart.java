package com.codefathers.cfkclient.controllers;

import com.codefathers.cfkclient.BackAbleController;
import com.codefathers.cfkclient.CFK;
import com.codefathers.cfkclient.CacheData;
import com.codefathers.cfkclient.dtos.customer.CartDTO;
import com.codefathers.cfkclient.dtos.customer.InCartDTO;
import com.codefathers.cfkclient.dtos.product.MiniProductDto;
import com.codefathers.cfkclient.utils.Connector;
import com.jfoenix.controls.JFXButton;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class Cart extends BackAbleController {
    @FXML private JFXButton back;
    @FXML private JFXButton minimize;
    @FXML private JFXButton close;
    @FXML private Label totalPrice;
    @FXML private JFXButton purchase;
    @FXML private TableView<InCartDTO> tableView;
    @FXML private TableColumn<InCartDTO, MiniProductDto> product;
    @FXML private TableColumn<InCartDTO, Integer> amount;
    @FXML private TableColumn<InCartDTO, Integer> price;
    @FXML private TableColumn<InCartDTO, Integer> afterOff;
    @FXML private TableColumn<InCartDTO, Integer> totalCol;
    @FXML private Button decrease;
    @FXML private Button increase;
    @FXML private Button delete;
    @FXML private Button goToProduct;

    private static CacheData cacheData = CacheData.getInstance();
    private static Connector connector = Connector.getInstance();

    @FXML
    private void initialize() {
        buttons();
        binds();
        load();
    }

    private void buttons() {
        close.setOnAction(e -> ((Stage) close.getScene().getWindow()).close());
        minimize.setOnAction(e -> ((Stage) close.getScene().getWindow()).setIconified(true));
        back.setOnAction(e -> handleBackButton());
        purchase.setOnAction(e -> purchase());
        delete.setOnAction(e -> delete());
        increase.setOnAction(e -> increaseDecrease(1));
        decrease.setOnAction(e -> increaseDecrease(-1));
        goToProduct.setOnAction(e -> viewProduct());
    }

    private void handleBackButton() {
        try {
            Scene scene = new Scene(CFK.loadFXML(back(), backForBackward()));
            CFK.setSceneToStage(back, scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void binds() {
        goToProduct.disableProperty().bind(Bindings.isEmpty(tableView.getSelectionModel().getSelectedItems()));
        increase.disableProperty().bind(Bindings.isEmpty(tableView.getSelectionModel().getSelectedItems()));
        decrease.disableProperty().bind(Bindings.isEmpty(tableView.getSelectionModel().getSelectedItems()));
        delete.disableProperty().bind(Bindings.isEmpty(tableView.getSelectionModel().getSelectedItems()));
    }

    private void load() {
        loadTable();
        loadTotalPrice();
    }

    private void loadTotalPrice() {
        totalPrice.setText("" + tableView.getItems().stream().mapToInt(InCartDTO::getTotal).sum() + "$");
    }

    private void purchase() {
        try {
            Scene scene = new Scene(CFK.loadFXML("Purchase", backForForward("Cart")));
            CFK.setSceneToStage(back, scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void delete() {
        InCartDTO inCartDTO = tableView.getSelectionModel().getSelectedItem();
        int productId = inCartDTO.getProduct().getId();
        try {
            connector.deleteProductFromCart(productId);
            tableView.getItems().remove(inCartDTO);
            tableView.refresh();
            loadTotalPrice();
        } catch (Exception e) {
            Notification.show("Error", e.getMessage(), back.getScene().getWindow(), true);
        }
    }

    private void viewProduct() {
        InCartDTO inCartDTO = tableView.getSelectionModel().getSelectedItem();
        int productId = inCartDTO.getProduct().getId();
        cacheData.setProductId(productId);
        try {
            Scene scene = new Scene(CFK.loadFXML("ProductDigest", backForForward("Cart")));
            CFK.setSceneToStage(new Stage(StageStyle.UNDECORATED), scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void increaseDecrease(int change) {
        InCartDTO inCartDTO = tableView.getSelectionModel().getSelectedItem();
        int productId = inCartDTO.getProduct().getId();
        try {
            connector.changeAmount(productId, change);
            int current = inCartDTO.getAmount();
            int tobe = current + change;
            if (tobe == 0) {
                tableView.getItems().remove(inCartDTO);
                tableView.refresh();
            } else {
                inCartDTO.setAmount(tobe);
                tableView.refresh();
            }
            loadTotalPrice();
        } catch (Exception e) {
            Notification.show("Error", e.getMessage(), back.getScene().getWindow(), true);
        }
    }

    private void loadTable() {
        product.setCellValueFactory(new PropertyValueFactory<>("product"));
        price.setCellValueFactory(new PropertyValueFactory<>("price"));
        afterOff.setCellValueFactory(new PropertyValueFactory<>("offPrice"));
        amount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        totalCol.setCellValueFactory(new PropertyValueFactory<>("total"));
        tableView.setItems(loadTableData());
        tableView.setPlaceholder(new Label("No Product In Cart :("));
    }

    private ObservableList<InCartDTO> loadTableData() {
        try {
            CartDTO pm = connector.showCart();
            return FXCollections.observableArrayList(pm.getPurchases());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
