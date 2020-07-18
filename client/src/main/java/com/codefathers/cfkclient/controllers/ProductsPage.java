package com.codefathers.cfkclient.controllers;

import com.codefathers.cfkclient.*;
import com.codefathers.cfkclient.dtos.category.CategoryPM;
import com.codefathers.cfkclient.dtos.product.FilterSortDto;
import com.codefathers.cfkclient.dtos.product.MiniProductDto;
import com.codefathers.cfkclient.dtos.product.OffProductPM;
import com.codefathers.cfkclient.dtos.product.SortType;
import com.codefathers.cfkclient.utils.Connector;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXToggleButton;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProductsPage extends BackAbleController {
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
    @FXML private ListView<CategoryPM> categories;
    @FXML private TableView<TableFeatureRow> featureTable;
    @FXML private TableColumn<TableFeatureRow, String> filterCol;
    @FXML private TableColumn<TableFeatureRow, TextField> fieldCol;
    @FXML private ScrollPane filterPane;
    @FXML private JFXToggleButton availableOnly;

    @FXML private TextField nameFilter;
    @FXML private TextField brandFilter;
    @FXML private TextField sellerFilter;
    @FXML private Button filterByFiled;


    private EventHandler<MouseEvent> productGetter = event ->
            loadProductOfCategory(categories.getSelectionModel().getSelectedItem().getId());

    private static Connector connector = Connector.getInstance();
    private static CacheData cacheData = CacheData.getInstance();

    @FXML
    private void initialize() {
        initLoad();
        ubButtons();
        binds();
        listeners();
        buttons();
    }

    private void buttons() {
        searchBut.setOnMouseClicked(productGetter);
        ignoreSearch.setOnAction(event -> resetFields());
    }

    private void resetFields() {
        featureTable.getItems().forEach(TableFeatureRow::reset);
        featureTable.refresh();
    }

    private void listeners() {
        categories.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            loadFeatures(newValue.getId());
            loadProductOfCategory(newValue.getId());
        });
        maxPrice.setOnMouseReleased(productGetter);
        minPrice.setOnMouseReleased(productGetter);
        offOnly.setOnMouseClicked(productGetter);
        EventHandler<ActionEvent> actionEventEventHandler = event -> SoundCenter.play(Sound.SWITCH);
        offOnly.setOnAction(actionEventEventHandler);
        ADOrder.selectedToggleProperty().addListener(observable -> productGetter.handle(null));
        type.selectedToggleProperty().addListener(observable -> productGetter.handle(null));
        availableOnly.setOnMouseClicked(productGetter);
        availableOnly.setOnAction(actionEventEventHandler);
        filterByFiled.setOnAction(event -> productGetter.handle(null));
    }

    private void loadFeatures(int id) {
        if (featureTable.isDisable()) featureTable.setDisable(false);
        try {
            ArrayList<String> features = connector.getSpecialFeatureOfCategory(id);
            List<TableFeatureRow> list = new ArrayList<>();
            features.forEach(f -> list.add(new TableFeatureRow(f)));
            ObservableList<TableFeatureRow> data = FXCollections.observableList(list);
            filterCol.setCellValueFactory(new PropertyValueFactory<>("feature"));
            fieldCol.setCellValueFactory(new PropertyValueFactory<>("value"));
            featureTable.setItems(data);
        } catch (Exception ignore) {}
    }

    private void loadProductOfCategory(int id) {
        if (offOnly.isSelected()) {
            loadOffProducts(id);
        } else {
            loadNormalProducts(id);
        }
    }

    private void loadOffProducts(int id) {
        FilterSortDto filter = createFilterPackage(id);
        try {
            List<OffProductPM> offs = connector.showAllOnOffProducts(filter);
            createOffProduct(offs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createOffProduct(List<OffProductPM> offs) {
        productVBox.getChildren().clear();
        List<List<OffProductPM>> chopped = chopped(offs, 4);
        chopped.forEach(sub -> productVBox.getChildren().add(row4ForBoxOff(sub)));
    }

    private Node row4ForBoxOff(List<OffProductPM> sub) {
        HBox hBox = new HBox();
        hBox.setSpacing(10);
        ArrayList<Parent> products = new ArrayList<>();
        sub.forEach(pm -> {
            try {
                Parent parent = SingleProductOnOff.generate(pm);
                products.add(parent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        hBox.getChildren().addAll(products);
        return hBox;
    }

    private void loadNormalProducts(int id) {
        FilterSortDto filter = createFilterPackage(id);
        try {
            List<MiniProductDto> list = connector.getAllProducts(filter);
            creatingMiniProducts(list);
        } catch (Exception e) {
            new OopsAlert().show(e.getMessage());
            e.printStackTrace();
        }
    }

    private void creatingMiniProducts(List<MiniProductDto> list) {
        productVBox.getChildren().clear();
        List<List<MiniProductDto>> subLists = chopped(list, 4);
        subLists.forEach(sublist -> productVBox.getChildren().add(row4ForBox(sublist)));
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
     * @param list should include 4 MiniProduct DTOs
     * @return {@link HBox} include 4 (Or Less) {@link SingleMiniProduct}
     */
    private HBox row4ForBox(List<MiniProductDto> list) {
        HBox hBox = new HBox();
        hBox.setSpacing(10);
        ArrayList<Parent> products = new ArrayList<>();
        list.forEach(pm -> {
            try {
                Parent parent = SingleMiniProduct.generate(pm);
                products.add(parent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        hBox.getChildren().addAll(products);
        return hBox;
    }

    private FilterSortDto createFilterPackage(int id) {
        FilterSortDto filterPackage = new FilterSortDto();
        int max = (int) maxPrice.getValue();
        int min = (int) minPrice.getValue();
        if (max == -1) {
            min = 0;
            max = 0;
        } else if (min == -1) {
            min = 0;
        }
        filterPackage.setCategoryId(id);
        filterPackage.setUpPriceLimit(max);
        filterPackage.setDownPriceLimit(min);
        filterPackage.setActiveFilters(CreateActiveFilter());
        filterPackage.setOffMode(offOnly.isSelected());
        filterPackage.setAvailableOnly(availableOnly.isSelected());
        if (!nameFilter.getText().isBlank()) {
            filterPackage.setName(nameFilter.getText());
        }
        if (!sellerFilter.getText().isBlank()) {
            filterPackage.setSeller(sellerFilter.getText());
        }
        if (!brandFilter.getText().isBlank()) {
            filterPackage.setBrand(brandFilter.getText());
        }
        filterPackage.setAscending(((RadioButton) ADOrder.getSelectedToggle()).getText().equals("Ascending"));
        filterPackage.setSortType(SortType.valueOF(((RadioButton) type.getSelectedToggle()).getText()));
        return filterPackage;
    }

    private HashMap<String, String> CreateActiveFilter() {
        HashMap<String, String> filters = new HashMap<>();
        featureTable.getItems().forEach(row -> {
            if (!row.value.getText().isBlank()) {
                filters.put(row.feature, row.value.getText());
            }
        });
        return filters;
    }

    private void binds() {
        cartButt.disableProperty().bind(cacheData.roleProperty.isEqualTo("Customer").not());
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
        filterPane.disableProperty().bind(Bindings.isEmpty(categories.getSelectionModel().getSelectedItems()));
    }

    private void ubButtons() {
        close.setOnAction(event -> ((Stage) close.getScene().getWindow()).close());
        minimize.setOnAction(event -> ((Stage) close.getScene().getWindow()).setIconified(true));
        back.setOnAction(event -> handleBack());
        cartButt.setOnAction(event -> handleCartButt());
    }

    private void handleCartButt() {
        try {
            Scene scene = new Scene(CFK.loadFXML("Cart", backForForward("ProductsPage")));
            CFK.setSceneToStage(cartButt, scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleBack() {
        try {
            Scene scene = new Scene(CFK.loadFXML(back(), backForBackward()));
            CFK.setSceneToStage(back, scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initLoad() {
        loadCategories();
    }

    private void loadCategories() {
        try {
            List<CategoryPM> list = connector.getAllCategories();
            ObservableList<CategoryPM> data = FXCollections.observableArrayList(list);
            categories.setItems(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


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
