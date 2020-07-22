package com.codefathers.cfkclient.controllers;

import com.codefathers.cfkclient.BackAbleController;
import com.codefathers.cfkclient.CFK;
import com.codefathers.cfkclient.CacheData;
import com.codefathers.cfkclient.dtos.auction.MiniAuctionDTO;
import com.codefathers.cfkclient.dtos.product.MiniProductDto;
import com.codefathers.cfkclient.utils.Connector;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXToggleButton;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// TODO : complete filters

public class AllAuctionPage extends BackAbleController {
    @FXML private ScrollPane filterPane;
    @FXML private ToggleGroup type;
    @FXML private ToggleGroup ADOrder;
    @FXML private Button filterByFiled;
    @FXML private TextField sellerFilter;
    @FXML private TextField brandFilter;
    @FXML private TextField nameFilter;
    @FXML private TableColumn fieldCol;
    @FXML private TableColumn filterCol;
    @FXML private TableView<ProductsPage.TableFeatureRow> featureTable;
    @FXML private JFXToggleButton availableOnly;
    @FXML private JFXSlider maxPrice;
    @FXML private JFXSlider minPrice;
    @FXML private VBox filterVBox;
    @FXML private VBox auctionVBox;
    @FXML private Button ignoreSearch;
    @FXML private Button searchButt;
    @FXML private JFXButton close;
    @FXML private JFXButton minimize;
    @FXML private JFXButton cart;
    @FXML private JFXButton back;

    private final CacheData cacheData = CacheData.getInstance();
    private final Connector connector = Connector.getInstance();

    @FXML
    public void initialize() {
        loadAuctions();
        initButts();
        bindings();
        listeners();
    }

    private void loadAuctions() {
        try {
            List<MiniAuctionDTO> dtos = connector.getAllAuctions();
            createMiniAuctions(dtos);
        } catch (Exception e) {
            Notification.show("Error", e.getMessage(), back.getScene().getWindow(), true);
        }
    }

    private void createMiniAuctions(List<MiniAuctionDTO> dtos) {
        auctionVBox.getChildren().clear();
        List<List<MiniAuctionDTO>> subLists = chopped(dtos, 4);
        subLists.forEach(sublist -> auctionVBox.getChildren().add(row4ForBox(sublist)));
    }

    private static <T> List<List<T>> chopped(List<T> list, final int L) {
        List<List<T>> parts = new ArrayList<>();
        final int N = list.size();
        for (int i = 0; i < N; i += L) {
            parts.add(new ArrayList<>(
                    list.subList(i, Math.min(N, i + L)))
            );
        }
        return parts;
    }

    /**
     * @param list should include 4 MiniAuction DTOs
     * @return {@link HBox} include 4 (Or Less) {@link SingleMiniAuction}
     */
    private HBox row4ForBox(List<MiniAuctionDTO> list) {
        HBox hBox = new HBox();
        hBox.setSpacing(10);
        ArrayList<Parent> auctions = new ArrayList<>();
        list.forEach(pm -> {
            try {
                Parent parent = SingleMiniAuction.generate(pm);
                auctions.add(parent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        hBox.getChildren().addAll(auctions);
        return hBox;
    }

    private void listeners() {

    }

    private void bindings() {
        cart.disableProperty().bind(cacheData.roleProperty.isEqualTo("Customer").not());
        minPrice.valueProperty().addListener((v, oldValue, newValue) -> {
            if (newValue.doubleValue() > maxPrice.getValue()) {
                maxPrice.setValue(newValue.doubleValue());
            }
        });
        maxPrice.valueProperty().addListener((v, oldValue, newValue) -> {
            if (newValue.doubleValue() < minPrice.getValue()) {
                minPrice.setValue(newValue.doubleValue());
            }
        });
    }

    private void initButts() {
        close.setOnAction(event -> ((Stage) close.getScene().getWindow()).close());
        minimize.setOnAction(event -> ((Stage) close.getScene().getWindow()).setIconified(true));
        back.setOnAction(event -> handleBack());
        cart.setOnAction(event -> handleCartButt());
    }

    private void handleBack() {
        try {
            Scene scene = new Scene(CFK.loadFXML(back(), backForBackward()));
            CFK.setSceneToStage(back, scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleCartButt() {
        try {
            Scene scene = new Scene(CFK.loadFXML("Cart", backForForward("ProductsPage")));
            CFK.setSceneToStage(cart, scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
