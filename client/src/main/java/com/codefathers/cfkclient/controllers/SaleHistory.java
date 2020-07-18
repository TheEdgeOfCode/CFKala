package com.codefathers.cfkclient.controllers;

import com.codefathers.cfkclient.BackAbleController;
import com.codefathers.cfkclient.CFK;
import com.codefathers.cfkclient.CacheData;
import com.codefathers.cfkclient.dtos.log.DeliveryStatus;
import com.codefathers.cfkclient.dtos.log.SellLogDTO;
import com.codefathers.cfkclient.utils.Connector;
import com.jfoenix.controls.JFXButton;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SaleHistory extends BackAbleController {
    @FXML
    private JFXButton back;
    @FXML private JFXButton minimize;
    @FXML private JFXButton close;
    @FXML private TableView<SellLogDTO> saleTable;
    @FXML private TableColumn<SellLogDTO, Date> sellNoCol;
    @FXML private TableColumn<SellLogDTO, Integer> sellDateCol;
    @FXML private Label totalSale;
    @FXML private VBox infoBox;
    @FXML private Label saleId;
    @FXML private Label productId;
    @FXML private JFXButton viewProduct;
    @FXML private Label date;
    @FXML private Label moneyGotten;
    @FXML private Label off;
    @FXML private Label buyer;
    @FXML private Label deliveryStatus;

    private final CacheData cacheData = CacheData.getInstance();

    @FXML
    public void initialize() {
        loadInformation();
        initButtons();
        listeners();
        binds();
    }

    private void binds() {
        infoBox.disableProperty().bind(Bindings.isEmpty(saleTable.getSelectionModel().getSelectedItems()));
        viewProduct.disableProperty().bind(Bindings.isEmpty(productId.textProperty()));
    }

    private void listeners() {
        saleTable.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                loadInfoBox(newValue);
            }
        });
    }

    private void loadInfoBox(SellLogDTO pm) {
        saleId.setText("" + pm.getId());
        productId.setText("" + pm.getProductId());
        date.setText(pm.getDate().toString());
        moneyGotten.setText("" + pm.getMoneyGotten() + "$");
        off.setText("" + pm.getDiscount() + "%");
        buyer.setText(pm.getBuyer());
        deliveryStatus.setText(pm.getDeliveryStatus().name());
    }

    private void loadInformation() {
        try {
            List<SellLogDTO> list = Connector.getInstance().viewSalesHistory();
            sellNoCol.setCellValueFactory(new PropertyValueFactory<>("id"));
            sellDateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
            ObservableList<SellLogDTO> data = FXCollections.observableArrayList(list);
            saleTable.setItems(data);
            loadTotalPrice(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadTotalPrice(List<SellLogDTO> list) {
        long total = list.stream().mapToLong(SellLogDTO::getMoneyGotten).sum();
        totalSale.setText("" + total + "$");
    }

    private List<SellLogDTO> logTest() {
        SellLogDTO SellLogDTO = new SellLogDTO(0, 12,
                13, 5, new Date(), "Ali", DeliveryStatus.DELIVERED);
        SellLogDTO SellLogDTO1 = new SellLogDTO(1, 13,
                20, 8, new Date(12345678), "SAPA", DeliveryStatus.DEPENDING);
        ArrayList<SellLogDTO> SellLogDTOS = new ArrayList<>();
        SellLogDTOS.add(SellLogDTO); SellLogDTOS.add(SellLogDTO1);
        return SellLogDTOS;
    }

    private void initButtons() {
        back.setOnAction(e -> handleBackButton());
        minimize.setOnAction(e -> minimize());
        close.setOnAction(e -> close());
        viewProduct.setOnAction(event -> loadProduct());
    }

    private void loadProduct() {
        int id = saleTable.getSelectionModel().getSelectedItem().getProductId();
        cacheData.setProductId(id);
        try {
            Scene scene = new Scene(CFK.loadFXML("ProductDigest", backForForward("SaleHistory")));
            CFK.setSceneToStage(new Stage(StageStyle.UNDECORATED), scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void close() {
        Stage window = (Stage) back.getScene().getWindow();
        window.close();
    }

    private void minimize() {
        Stage window = (Stage) back.getScene().getWindow();
        window.setIconified(true);
    }

    private void handleBackButton() {
        try {
            CFK.setRoot("SellerAccount");
        } catch (IOException ignore) {}
    }
}
