package com.codefathers.cfkclient.controllers;

import com.codefathers.cfkclient.BackAbleController;
import com.codefathers.cfkclient.CFK;
import com.codefathers.cfkclient.CacheData;
import com.codefathers.cfkclient.dtos.customer.OrderLogDTO;
import com.codefathers.cfkclient.dtos.customer.OrderProductDTO;
import com.codefathers.cfkclient.dtos.log.DeliveryStatus;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.codefathers.cfkclient.controllers.Notification.show;

public class OrderHistory extends BackAbleController {
    @FXML
    private JFXButton back;
    @FXML private JFXButton cartButt;
    @FXML private JFXButton minimize;
    @FXML private JFXButton close;

    @FXML private Button viewProduct;
    @FXML private Button giveScore;
    @FXML private TableView<OrderLogDTO> orderTable;
    @FXML private TableColumn<OrderLogDTO, Integer> orderNoColumn;
    @FXML private TableColumn<OrderLogDTO, String> dateColumn;

    @FXML private Label no;
    @FXML private Label date;
    @FXML private Label price;
    @FXML private Label discount;
    @FXML private Label delStatus;

    @FXML private TableView<OrderProductDTO> productsTable;
    @FXML private TableColumn<OrderProductDTO, String> pNameCol;
    @FXML private TableColumn<OrderProductDTO, String> pSellerCol;
    @FXML private TableColumn<OrderProductDTO, Integer> pPriceCol;

    private Connector connector = Connector.getInstance();
    private CacheData cacheData = CacheData.getInstance();

    @FXML
    public void initialize() {
        initButts();
        initOrdersTable();
    }

    private void initOrdersTable() {
        orderNoColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        orderTable.setItems(getOrders());

        orderTable.getSelectionModel().selectedItemProperty().addListener((v, oldOrder, newOrder) -> changeData(newOrder));
    }

    private void changeData(OrderLogDTO order) {
        no.setText(String.valueOf(order.getOrderId()));
        date.setText(order.getDate());
        price.setText(String.valueOf(order.getPrice()));
        discount.setText(String.valueOf(order.getDiscount()));
        delStatus.setText(order.getDeliveryStatus());
        changeProductTable(order.getOrderProductDTOS());
    }

    private void changeProductTable(List<OrderProductDTO> products) {
        pNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        pSellerCol.setCellValueFactory(new PropertyValueFactory<>("seller"));
        pPriceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        productsTable.setItems(getProducts(products));
    }

    private ObservableList<OrderProductDTO> getProducts(List<OrderProductDTO> productPMS) {
        ObservableList<OrderProductDTO> products = FXCollections.observableArrayList();
        products.addAll(productPMS);
        return products;
    }

    private ObservableList<OrderLogDTO> getOrders() {
        ObservableList<OrderLogDTO> orders = FXCollections.observableArrayList();

        try {
            orders.addAll(connector.showOrders());
        } catch (Exception e) {
            show("Error", e.getMessage(), back.getScene().getWindow(), true);
            e.printStackTrace();
        }

        //orders.addAll(loadOrders());

        return orders;
    }

    private ArrayList<OrderProductDTO> loadProducts() {
        ArrayList<OrderProductDTO> products = new ArrayList<>();
        products.add(new OrderProductDTO(2, "pashmak", "hatam", 1000));
        products.add(new OrderProductDTO(23, "dullForKimmi", "marmof", 25000));
        products.add(new OrderProductDTO(2, "skirtForKimmi", "sapa", 35000));
        products.add(new OrderProductDTO(2, "sweetForKimmi", "marmof", 500));

        return products;
    }

    private ArrayList<OrderLogDTO> loadOrders() {
        ArrayList<OrderLogDTO> orders = new ArrayList<>();
        orders.add(new OrderLogDTO(1552, "2007-12-14 13:25:44", loadProducts(), DeliveryStatus.DELIVERED.toString(), 150000, 200000, 10));
        orders.add(new OrderLogDTO(1342, "2003-05-16 19:14:45", loadProducts(), DeliveryStatus.DEPENDING.toString(), 20000, 1244, 50));
        orders.add(new OrderLogDTO(1226, "2003-03-23 14:33:03", loadProducts(), DeliveryStatus.DELIVERED.toString(), 32500, 32555, 0));
        orders.add(new OrderLogDTO(2588, "2012-09-12 21:23:22", loadProducts(), DeliveryStatus.DELIVERED.toString(), 13700, 45200, 2));

        return orders;
    }

    private void initButts() {
        minimize.setOnAction(e -> {
            Stage stage = (Stage) minimize.getScene().getWindow();
            stage.setIconified(true);
        });
        close.setOnAction(e -> {
            Stage stage = (Stage) close.getScene().getWindow();
            stage.close();
        });
        back.setOnAction(event -> handleBack());
        cartButt.setOnAction(event -> handleCart());
        viewProduct.setOnAction(event -> handleViewProduct());
        viewProduct.disableProperty().bind(Bindings.isEmpty(productsTable.getSelectionModel().getSelectedCells()));
        giveScore.setOnAction(event -> giveScoreToProduct());
        giveScore.disableProperty().bind(Bindings.isEmpty(productsTable.getSelectionModel().getSelectedCells()));
    }

    private void giveScoreToProduct() {
        int score = new ScoreGetter().show();
        int productId = productsTable.getSelectionModel().getSelectedItem().getId();
        if (score != 0) {
            try {
                connector.assignAScore(productId + "," + score);
            } catch (Exception e) {
                show("Error", e.getMessage(), back.getScene().getWindow(), true);
            }
            show("Successful", "Your Score was Submitted!!!", back.getScene().getWindow(), false);
        }
    }

    private void handleViewProduct() {
        try {
            CFK.setRoot("ProductDigest");
            cacheData.setProductId(productsTable.getSelectionModel().getSelectedItem().getId());
        } catch (IOException e) {
            show("Error", e.getMessage(), back.getScene().getWindow(), true);
            e.printStackTrace();
        }
    }

    private void handleCart() {
        try {
            CFK.setRoot("Cart");
        } catch (IOException e) {
            show("Error", e.getMessage(), back.getScene().getWindow(), true);
            e.printStackTrace();
        }
    }

    private void handleBack() {
        try {
            Scene scene = new Scene(CFK.loadFXML(back(), backForBackward()));
            CFK.setSceneToStage(back, scene);
        } catch (IOException e) {
            show("Error", e.getMessage(), back.getScene().getWindow(), true);
            e.printStackTrace();
        }
    }
    
}
