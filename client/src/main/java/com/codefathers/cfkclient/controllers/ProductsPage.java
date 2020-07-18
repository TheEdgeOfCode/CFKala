package com.codefathers.cfkclient.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXToggleButton;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class ProductsPage {
    @FXML
    private JFXButton back;
    @FXML private JFXButton cartButt;
    @FXML private JFXButton minimize;
    @FXML private JFXButton close;
    @FXML private VBox filterVBox;
    @FXML private JFXSlider minPrice;
    @FXML private JFXSlider maxPrice;
    @FXML private ToggleGroup ADOrder;
    @FXML private ToggleGroup type;
    @FXML private VBox productVBox;
    @FXML private Button searchBut;
    @FXML private Button ignoreSearch;
    @FXML private JFXToggleButton offOnly;
    @FXML private ListView categories;
    @FXML private TableView<TableFeatureRow> featureTable;
    @FXML private TableColumn<TableFeatureRow, String> filterCol;
    @FXML private TableColumn<TableFeatureRow, TextField> fieldCol;
    @FXML private ScrollPane filterPane;
    @FXML private JFXToggleButton avaiOnly;

    @FXML private TextField nameFilter;
    @FXML private TextField brandFilter;
    @FXML private TextField sellerFilter;
    @FXML private Button filterByFiled;


    public static class TableFeatureRow {
        private String feature;
        private TextField value;

        TableFeatureRow(String feature) {
            this.feature = feature;
            value = new TextField();
        }

        public String getFeature() {
            return feature;
        }

        public void setFeature(String feature) {
            this.feature = feature;
        }

        public TextField getValue() {
            return value;
        }

        public void setValue(TextField value) {
            this.value = value;
        }

        public void reset() {
            value.clear();
        }
    }
}
