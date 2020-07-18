package com.codefathers.cfkclient.controllers;

import com.codefathers.cfkclient.BackAbleController;
import com.codefathers.cfkclient.CFK;
import com.codefathers.cfkclient.CacheData;
import com.codefathers.cfkclient.dtos.category.CategoryPM;
import com.codefathers.cfkclient.dtos.product.AddSellerToProductDTO;
import com.codefathers.cfkclient.dtos.product.CreateProductDTO;
import com.codefathers.cfkclient.dtos.product.MicroProductDto;
import com.codefathers.cfkclient.utils.Connector;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NewProduct extends BackAbleController {
    @FXML
    private ComboBox<CategoryPM> category;
    @FXML private JFXTextField productName;
    @FXML private JFXTextField price;
    @FXML private JFXTextField stock;
    @FXML private JFXTextField company;
    @FXML private JFXTextArea description;
    @FXML private TableView<ProductsPage.TableFeatureRow> table;
    @FXML private TableColumn<ProductsPage.TableFeatureRow, String> Features;
    @FXML private TableColumn<ProductsPage.TableFeatureRow, TextField> TextFields;
    @FXML private JFXButton createProduct;
    @FXML private JFXButton back;
    @FXML private Button view;
    @FXML private Button sellThis;
    @FXML private VBox sellThisBox;
    @FXML private ListView<MicroProductDto> similarProduct;
    @FXML private ListView<File> pictureList;
    @FXML private JFXButton pickPic;
    @FXML private JFXButton reset;

    private static final Paint redColor = Paint.valueOf("#c0392b");
    private static final Paint blueColor = Paint.valueOf("#405aa8");
    private final CacheData cacheData = CacheData.getInstance();
    private final Connector connector = Connector.getInstance();

    @FXML
    public void initialize() {
        initButtons();
        initFields();
        initCategoryBox();
        initializeListeners();
    }

    private void initButtons() {
        createProduct.setOnAction(event -> handleCreate());
        view.setOnAction(event -> handleView());
        sellThis.setOnAction(event -> handleSellThis());
        back.setOnAction(event -> handleBack());
        pickPic.setOnAction(event -> pickPictures());
        reset.setOnAction(event -> deleteSelectedImage());
    }

    private void initFields() {
        resetSettingForFields(productName,"Product Name");
        resetSettingForFields(price,"Price");
        resetSettingForFields(stock,"Stock");
        resetSettingForFields(company,"Company");
    }

    private void initCategoryBox() {
        ObservableList<CategoryPM> categories = FXCollections.observableArrayList();
        try {
            ArrayList<CategoryPM> allCats = connector.getAllCategories();
            categories.addAll(allCats);
            category.setItems(categories);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeListeners() {
        category.getSelectionModel().selectedItemProperty().addListener((v, oldValue,newValue)->{
            changeFeaturesTable(newValue.getId());
        });
        productName.focusedProperty().addListener((v, oldValue,newValue)->{
            if (!newValue)fullSellThis(productName.getText());
        });
        sellThis.disableProperty().bind(Bindings.isEmpty(similarProduct.getSelectionModel().getSelectedItems()));
        view.disableProperty().bind(Bindings.isEmpty(similarProduct.getSelectionModel().getSelectedItems()));
        createProduct.disableProperty().bind(Bindings.isEmpty(pictureList.getItems()));
        reset.disableProperty().bind(Bindings.isEmpty(pictureList.getSelectionModel().getSelectedItems()));
    }

    private void fullSellThis(String entry){
        if (entry.isEmpty()){
            sellThisBox.setDisable(true);
            similarProduct.setItems(FXCollections.observableArrayList());
        }else {
            ArrayList<MicroProductDto> microProducts = null;
            try {
                microProducts = connector.similarNameProducts(entry);
                if (microProducts.isEmpty()){
                    sellThisBox.setDisable(true);
                } else {
                    sellThisBox.setDisable(false);
                    ObservableList<MicroProductDto> data = FXCollections.observableArrayList(microProducts);
                    similarProduct.setItems(data);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void changeFeaturesTable(int categoryId) {
        if (table.isDisable())table.setDisable(false);
        try {
            ArrayList<String> features = connector.getSpecialFeatureOfCategory(categoryId);
            List<ProductsPage.TableFeatureRow> list = new ArrayList<>();
            features.forEach(f -> list.add(new ProductsPage.TableFeatureRow(f)));
            ObservableList<ProductsPage.TableFeatureRow> data = FXCollections.observableList(list);
            Features.setCellValueFactory(new PropertyValueFactory<>("feature"));
            TextFields.setCellValueFactory(new PropertyValueFactory<>("value"));
            table.setItems(data);
        } catch (Exception ignore) {}
    }

    private void resetSettingForFields(JFXTextField field, String prompt) {
        field.textProperty().addListener(e -> {
            field.setFocusColor(blueColor);
            field.setPromptText(prompt);
        });
    }

    private void deleteSelectedImage() {
        pictureList.getItems().remove(pictureList.getSelectionModel().getSelectedItem());
        Notification.show("Successful",
                "The Pics were Successfully Removed from the Product!!!",
                back.getScene().getWindow(),
                false);
    }

    private void pickPictures() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image", "*.jpg"));
        List<File> files = fileChooser.showOpenMultipleDialog(null);
        ObservableList<File> data = FXCollections.observableArrayList(files);
        pictureList.getItems().addAll(data);
        Notification.show("Successful", "The Pics were Successfully Added to the Product!!!", back.getScene().getWindow(), false);
    }

    private void handleBack() {
        try {
            Scene scene = new Scene(CFK.loadFXML(back(), backForBackward()));
            CFK.setSceneToStage(back, scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleSellThis() {
        if (checkPriceStock()){
            int productId = similarProduct.getSelectionModel().getSelectedItem().getId();
            int amount = Integer.parseInt(stock.getText());
            int productPrice = Integer.parseInt(price.getText());
            AddSellerToProductDTO addSellerToProductDTO = new AddSellerToProductDTO(productId, amount, productPrice);
            try {
                connector.becomeSellerOfExistingProduct(addSellerToProductDTO);
                try {
                    Scene scene = new Scene(CFK.loadFXML(back(), backForBackward()));
                    CFK.setSceneToStage(back, scene);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                Notification.show("Error", e.getMessage(), back.getScene().getWindow(), true);
            }
        }
    }

    private boolean checkPriceStock() {
        if (price.getText().isEmpty()){
            errorField(price,"Price Is Required");
            return false;
        } else if (!price.getText().matches("\\d{0,9}")){
            errorField(price,"Price Must Be Numerical");
            return false;
        } else if (stock.getText().isEmpty()) {
            errorField(stock,"Stock Is Required");
            return false;
        } else if (!stock.getText().matches("\\d{0,9}")){
            errorField(price,"Stock Must Be Numerical");
            return false;
        } else return true;
    }

    private void errorField(JFXTextField field,String prompt){
        field.setPromptText(prompt);
        field.setFocusColor(redColor);
        field.requestFocus();
    }

    private void errorField(JFXTextArea area,String prompt) {
        area.setFocusColor(redColor);
        area.setPromptText(prompt);
        area.requestFocus();
    }

    private void handleView() {
        int productId = similarProduct.getSelectionModel().getSelectedItem().getId();
        cacheData.setProductId(productId);
        Stage window = new Stage();
        try {
            Scene scene = new Scene(CFK.loadFXML("ProductDigest"));
            window.setScene(scene);
            scene.setFill(Color.TRANSPARENT);
            window.initStyle(StageStyle.TRANSPARENT);
            window.show();
        } catch (IOException ignore) {}
    }

    private CreateProductDTO generateProductInfoPack(HashMap<String, String> publicFeatures, HashMap<String, String> specialFeatures) {
        return new CreateProductDTO(
                cacheData.getUsername(),
                productName.getText(),
                Integer.parseInt(company.getText()),
                category.getSelectionModel().getSelectedItem().getId(),
                description.getText(),
                Integer.parseInt(stock.getText()),
                Integer.parseInt(price.getText()),
                publicFeatures,
                specialFeatures);
    }

    private void handleCreate() {
        if (checkForEmptyValues()) {
            String[] productInfo = new String[7];
            HashMap<String, String> publicFeatures = new HashMap<>();
            HashMap<String, String> specialFeatures = new HashMap<>();
            CreateProductDTO createProductDTO = generateProductInfoPack(publicFeatures, specialFeatures);
            generateFeaturePacks(publicFeatures, specialFeatures);
            try {
                try {
                    int productId = connector.createProduct(createProductDTO);
                    savePics(productId);
                    Notification.show("Successful", "Your Product was Registered Successfully!!!",
                            back.getScene().getWindow(), false);
                    try {
                        Scene scene = new Scene(CFK.loadFXML(back(), backForBackward()));
                        CFK.setSceneToStage(back, scene);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    Notification.show("Error", e.getMessage(), back.getScene().getWindow(), true);
                }
            } catch (RuntimeException e) {
                Notification.show("Error", e.getMessage(), back.getScene().getWindow(), true);
            }
        }
    }

    private void savePics(int id) {
        ArrayList<File> files = new ArrayList<>(pictureList.getItems());
        ArrayList<InputStream> streams = new ArrayList<>();
        files.forEach(file -> {
            try {
                streams.add(new FileInputStream(file));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });
        try {
            connector.updateProductMainImage(id, (InputStream[])streams.toArray());
        } catch (Exception e) {
            Notification.show("Error",e.getMessage(),back.getScene().getWindow(),true);
        }
    }

    private void generateFeaturePacks(HashMap<String, String> publicFeatures, HashMap<String, String> specialFeatures) {
        ArrayList<ProductsPage.TableFeatureRow> list = new ArrayList<>(table.getItems());
        try {
            ArrayList<String> publicTitles = (ArrayList<String>) connector.getPublicFeaturesOfCategory();
            int index;
            for (ProductsPage.TableFeatureRow row : list) {
                String title = row.getFeature();
                String feature = row.getValue().getText();
                index = publicTitles.indexOf(title);
                if (index != -1) {
                    publicFeatures.put(title, feature);
                    publicTitles.remove(index);
                }else {
                    specialFeatures.put(title, feature);
                }
            }
        } catch (Exception e) {
            Notification.show("Error", e.getMessage(), back.getScene().getWindow(), true);
        }
    }

    private boolean checkForEmptyValues(){
        if (productName.getText().isEmpty()){
            errorField(productName,"Product Name Is Required");
            return false;
        } else if (category.getSelectionModel().getSelectedItem() == null){
            category.setPromptText("Select A Category!!");
            category.requestFocus();
            return false;
        } else if (price.getText().isEmpty()){
            errorField(price,"Price Is Required");
            return false;
        } else if (!price.getText().matches("\\d{0,9}")){
            errorField(price,"Price Must Be Numerical");
            return false;
        } else if (stock.getText().isEmpty()) {
            errorField(stock,"Stock Is Required");
            return false;
        } else if (!stock.getText().matches("\\d{0,9}")){
            errorField(price,"Stock Must Be Numerical");
            return false;
        } else if (company.getText().isEmpty()) {
            errorField(company,"Company Is Required");
            return false;
        } else if (description.getText().isEmpty()){
            errorField(description,"Description Is Required");
            return false;
        }
        else return true;
    }
}
