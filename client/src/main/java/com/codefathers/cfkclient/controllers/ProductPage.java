package com.codefathers.cfkclient.controllers;

import com.codefathers.cfkclient.BackAbleController;
import com.codefathers.cfkclient.CFK;
import com.codefathers.cfkclient.CacheData;
import com.codefathers.cfkclient.dtos.product.FilterSortDto;
import com.codefathers.cfkclient.dtos.product.MiniProductDto;
import com.codefathers.cfkclient.dtos.product.SortType;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSlider;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class ProductPage extends BackAbleController {
    @FXML
    private ToggleGroup ADOrder;
    @FXML private ToggleGroup type;
    @FXML private JFXButton back ;
    @FXML private JFXButton minimize;
    @FXML private JFXButton close;
    @FXML private VBox panelVbox;
    @FXML private JFXComboBox<String> color;
    @FXML private JFXSlider minPrice;
    @FXML private JFXSlider maxPrice;

    @FXML
    public void initialize() {
        initButtons();
        loadProducts();
    }

    private void loadProducts() {
        loadInformation();
        ADOrder.selectedToggleProperty().addListener((v, oldValue, newValue) ->
                loadInformation());
        type.selectedToggleProperty().addListener((v, oldValue, newValue) ->
                loadInformation());
        minPrice.setOnMouseDragged(mouseEvent -> loadInformation());
        minPrice.setOnMousePressed(mouseEvent -> loadInformation());
        maxPrice.setOnMousePressed(mouseEvent -> loadInformation());
        maxPrice.setOnMouseDragged(mouseEvent -> loadInformation());
        color.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) ->
                loadInformation());
    }

    private void createListsOfProductsInVBox(List<MiniProductDto> products) {
        /*for (MiniProductPM product : products) {
            try {
                panelVbox.getChildren().add(ProductRowForSM.generate(product.getName(), product.getId()));
            } catch (IOException ignore) {}
        }*/
    }

    private void loadInformation() {
        /*panelVbox.getChildren().clear();
        FilterSortDto filterSortDto = makeFilterSortDTO();
        CacheData cacheData = CacheData.getInstance();
        try {
            switch (cacheData.getRole()) {
                case "Seller":
                case "seller":
                    List<MiniProductDto> productPMS = SellerController.getInstance().manageProducts(cacheData.getUsername() *//*"Ali"*//*, sortPackage, filterPackage);
                    createListsOfProductsInVBox(productPMS);
                    break;
                case "manager":
                case "Manager":
                    List<MiniProductPM> productPMSManager = ManagerController.getInstance().manageProducts(sortPackage, filterPackage);
                    createListsOfProductsInVBox(productPMSManager);
                    break;
            }

        } catch (UserNotAvailableException e) {
            new OopsAlert().show(e.getMessage());
        }*/
    }

    private FilterSortDto makeFilterSortDTO() {
        FilterSortDto filterSortDto = new FilterSortDto();
        if (color.getValue() != null) {
            filterSortDto.getActiveFilters().put("Color", color.getValue());
        } if (minPrice.getValue() >= maxPrice.getValue()) {
            maxPrice.setValue(minPrice.getValue());
        }
        filterSortDto.setCategoryId(0);
        filterSortDto.setUpPriceLimit((int) maxPrice.getValue());
        filterSortDto.setDownPriceLimit((int) minPrice.getValue());
        filterSortDto.setAscending(ADOrder.selectedToggleProperty().toString().contains("Ascending"));
        if (type.selectedToggleProperty().toString().contains("Price"))
            filterSortDto.setSortType(filterSortDto.isAscending() ? SortType.MORE_PRICE : SortType.LESS_PRICE);
        else if (type.selectedToggleProperty().toString().contains("Date Added"))
            filterSortDto.setSortType(SortType.TIME);
        else if (type.selectedToggleProperty().toString().contains("View"))
            filterSortDto.setSortType(SortType.VIEW);
        else if (type.selectedToggleProperty().toString().contains("Bought"))
            filterSortDto.setSortType(SortType.BOUGHT_AMOUNT);
        else if (type.selectedToggleProperty().toString().contains("Name"))
            filterSortDto.setSortType(SortType.NAME);
        else filterSortDto.setSortType(SortType.SCORE);
        return filterSortDto;
    }

    /*private FilterPackage makeFilterPackage() {
        FilterPackage filterPackage = new FilterPackage();
        if (color.getValue() != null) {
            filterPackage.getActiveFilters().put("Color", color.getValue());
        }
        if (minPrice.getValue() >= maxPrice.getValue()) {
            maxPrice.setValue(minPrice.getValue());
        }
        filterPackage.setCategoryId(0);
        filterPackage.setUpPriceLimit((int)maxPrice.getValue());
        filterPackage.setDownPriceLimit((int)minPrice.getValue());
        return filterPackage;
    }*/

    /*private SortPackage makeSortPackage() {
        SortPackage sortPackage = new SortPackage();
        sortPackage.setAscending(ADOrder.selectedToggleProperty().toString().contains("Ascending"));
        if (type.selectedToggleProperty().toString().contains("Price")) {
            if (sortPackage.isAscending())
                sortPackage.setSortType(SortType.MORE_PRICE);
            else sortPackage.setSortType(SortType.LESS_PRICE);
        } else if (type.selectedToggleProperty().toString().contains("Date Added")) {
            sortPackage.setSortType(SortType.TIME);
        } else if (type.selectedToggleProperty().toString().contains("View")) {
            sortPackage.setSortType(SortType.VIEW);
        } else if (type.selectedToggleProperty().toString().contains("Bought")) {
            sortPackage.setSortType(SortType.BOUGHT_AMOUNT);
        } else if (type.selectedToggleProperty().toString().contains("Name")) {
            sortPackage.setSortType(SortType.NAME);
        } else sortPackage.setSortType(SortType.SCORE);
        return sortPackage;
    }*/

    private void initButtons() {
        back.setOnAction(e -> handleBack());
        minimize.setOnAction(e -> minimize());
        close.setOnAction(e -> close());
    }

    private void close() {
        Stage window = (Stage) back.getScene().getWindow();
        window.close();;
    }

    private void minimize() {
        Stage window = (Stage) back.getScene().getWindow();
        window.setIconified(true);
    }

    private void handleBack() {
        try {
            Scene scene = new Scene(CFK.loadFXML(back(), backForBackward()));
            CFK.setSceneToStage(back, scene);
        } catch (IOException ignore) {}
    }


}
