package com.codefathers.cfkclient.controllers;

import com.codefathers.cfkclient.BackAbleController;
import com.codefathers.cfkclient.CFK;
import com.codefathers.cfkclient.CacheData;
import com.codefathers.cfkclient.dtos.edit.ProductEditAttribute;
import com.codefathers.cfkclient.dtos.product.FullProductPM;
import com.codefathers.cfkclient.dtos.product.MiniProductDto;
import com.codefathers.cfkclient.dtos.product.SellPackageDto;
import com.codefathers.cfkclient.exeptions.NotValidFieldException;
import com.codefathers.cfkclient.utils.Connector;
import com.jfoenix.controls.JFXButton;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.codefathers.cfkclient.controllers.Notification.show;

public class EditProduct extends BackAbleController {
    @FXML
    private JFXButton back;
    @FXML private JFXButton minimize;
    @FXML private JFXButton close;
    @FXML private Rectangle imageViewer;
    @FXML private ListView<Image> imageList;

    @FXML private JFXButton addPicture;
    @FXML private JFXButton removePicture;
    @FXML private JFXButton submitPicChange;

    @FXML private TableView<FeatureRow> featureTable;
    @FXML private TableColumn<FeatureRow, String> featureCol;
    @FXML private TableColumn<FeatureRow, String> currentValCol;
    @FXML private TableColumn<FeatureRow, String> EdValCol;

    @FXML private TextField editVal;
    @FXML private JFXButton editFeatureBtn;
    @FXML private JFXButton resetTable;
    @FXML private JFXButton submitFeatures;
    @FXML private TextField name;
    @FXML private ChoiceBox<String> colorBox;
    @FXML private TextField weigh;
    @FXML private TextField dimension;
    @FXML private TextField price;
    @FXML private TextField stock;
    @FXML private JFXButton submitMain;


    private int id;
    private static Connector connector = Connector.getInstance();

    @FXML
    public void initialize() {
        id = CacheData.getInstance().getProductId();
        loadProduct();
        listeners();
        binds();
        upButtons();
        pictureSection();
        mainSection();
        featureSection();
    }

    private void featureSection() {
        editFeatureBtn.setOnAction(event -> editFeature());
        resetTable.setOnAction(event -> resetTableAction());
        submitFeatures.setOnAction(event -> submitFeaturesAction());
    }

    private void submitFeaturesAction() {
        try {
            ProductEditAttribute attribute = createPackageForFeatures();
            connector.editProduct(attribute);
            show("Successful", "Request Sent To Manager Successfully",
                    back.getScene().getWindow(), false);
        } catch (Exception e) {
            show("Error", e.getMessage(), back.getScene().getWindow(), true);
            e.printStackTrace();
        }
    }

    private ProductEditAttribute createPackageForFeatures() {
        ProductEditAttribute attribute = new ProductEditAttribute();
        attribute.setSourceId(id);
        Map<String, String> features = new HashMap<>();
        featureTable.getItems().forEach(featureRow -> {
            if (!featureRow.getNewValue().isBlank()) {
                features.put(featureRow.feature, featureRow.newValue);
            }
        });
        attribute.setSpecialFeatures(features.size() == 0 ? null : features);
        return attribute;
    }

    private void resetTableAction() {
        featureTable.getItems().forEach(featureRow -> featureRow.setNewValue(""));
        featureTable.refresh();
    }

    private void editFeature() {
        FeatureRow item = featureTable.getSelectionModel().getSelectedItem();
        item.setNewValue(editVal.getText());
        featureTable.refresh();
    }

    private void mainSection() {
        submitMain.setOnAction(event -> {
            try {
                ProductEditAttribute attribute = createPackageForMain();
                connector.editProduct(attribute);
                show("Successful", "Request Sent To Manager Successfully",
                        back.getScene().getWindow(), false);
            } catch (Exception e) {
                show("Error", e.getMessage(), back.getScene().getWindow(), true);
                e.printStackTrace();
            }
        });
    }

    private ProductEditAttribute createPackageForMain() throws NotValidFieldException {
        ProductEditAttribute attribute = new ProductEditAttribute();
        attribute.setSourceId(id);
        Map<String, String> features = new HashMap<>();
        if (!name.getText().isBlank()) {
            attribute.setName(name.getText());
        }
        if (!dimension.getText().isEmpty()) {
            features.put("Dimension", dimension.getText());
        }
        if (!weigh.getText().isEmpty()) {
            features.put("Weigh", weigh.getText());
        }
        if (!price.getText().isEmpty()) {
            String price = this.price.getText();
            if (price.matches("\\d{0,9}")) {
                attribute.setNewPrice(Integer.parseInt(price));
            } else {
                throw new NotValidFieldException("price", " positive integer");
            }
        }
        if (!stock.getText().isEmpty()) {
            String stock = this.stock.getText();
            if (stock.matches("\\d{0,9}")) {
                attribute.setNewStock(Integer.parseInt(stock));
            } else {
                throw new NotValidFieldException("stock", " positive integer");
            }
        }
        if (colorBox.getSelectionModel().getSelectedItem() != null) {
            features.put("Color", colorBox.getSelectionModel().getSelectedItem());
        }
        attribute.setPublicFeatures(features.size() == 0 ? null : features);
        return attribute;
    }

    private void pictureSection() {
        removePicture.setOnAction(event -> deletePicture());
        addPicture.setOnAction(event -> addPictureToList());
        submitPicChange.setOnAction(event -> submitEditPicture());
    }

    private void submitEditPicture() {
        List<Image> images = new ArrayList<>(imageList.getItems());
        ArrayList<InputStream> others = new ArrayList<>();
        images.forEach(image -> others.add(imageToInputStream(image)));
        try {
            connector.updateProductMainImage(id,(InputStream[]) others.toArray());
            show("Successful", "Images Saved To Product", back.getScene().getWindow(), false);
        } catch (Exception e) {
            show("Error", e.getMessage(), back.getScene().getWindow(), true);
        }
    }

    private InputStream imageToInputStream(Image image) {
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, "jpeg", os);
            return new ByteArrayInputStream(os.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void addPictureToList() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image", "*.jpg"));
        List<File> files = fileChooser.showOpenMultipleDialog(null);
        if (files != null) {
            List<Image> images = createImagesFromFiles(files);
            imageList.getItems().addAll(images);
        }
    }

    private List<Image> createImagesFromFiles(List<File> files) {
        List<Image> images = new ArrayList<>();
        files.forEach(file -> {
            Image image = new Image(file.toURI().toString());
            images.add(image);
        });
        return images;
    }

    private void deletePicture() {
        Image current = imageList.getSelectionModel().getSelectedItem();
        if (current != null) {
            imageList.getItems().remove(current);
        }
    }

    private void upButtons() {
        back.setOnAction(event -> handleBack());
        close.setOnAction(event -> {
            Stage stage = (Stage) close.getScene().getWindow();
            stage.close();
        });
        minimize.setOnAction(event -> {
            Stage stage = (Stage) close.getScene().getWindow();
            stage.setIconified(true);
        });
    }

    private void handleBack() {
        try {
            Scene scene = new Scene(CFK.loadFXML(back(), backForBackward()));
            CFK.setSceneToStage(back, scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void binds() {
        pictureSectionBinds();
        mainSectionBinds();
        featureSectionBinds();
    }

    private void featureSectionBinds() {
        editFeatureBtn.disableProperty().bind(Bindings.isEmpty(editVal.textProperty()));
        editVal.disableProperty().bind(Bindings.isEmpty(
                featureTable.getSelectionModel().getSelectedItems()
        ));
    }

    private void mainSectionBinds() {
        submitMain.disableProperty().bind(
                Bindings.isEmpty(name.textProperty())
                        .and(colorBox.showingProperty().not())
                        .and(Bindings.isEmpty(weigh.textProperty()))
                        .and(Bindings.isEmpty(dimension.textProperty()))
                        .and(Bindings.isEmpty(price.textProperty()))
                        .and(Bindings.isEmpty(stock.textProperty()))
        );
    }

    private void pictureSectionBinds() {
        removePicture.disableProperty().bind(Bindings.isEmpty(imageList.getSelectionModel().getSelectedItems()));
        submitPicChange.disableProperty().bind(Bindings.isEmpty(imageList.getItems()));
    }

    private void listeners() {
        pictureListener();
    }

    private void pictureListener() {
        imageList.getSelectionModel().selectedItemProperty().addListener((v, o, n) -> {
            if (n != null) {
                imageViewer.setFill(new ImagePattern(n));
            }
        });
    }

    private void loadProduct() {
        try {
            FullProductPM pm = connector.viewAttributes(id);
            loadMainSection(pm.getProduct(), pm.getFeatures());
            loadPictureSection();
            loadFeatureSection(pm.getFeatures());
        } catch (Exception e) {
            new OopsAlert().show(e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadPictureSection() {
        try {
            ArrayList<Image> images = connector.loadImages(id);
            ObservableList<Image> data = FXCollections.observableArrayList(images);
            imageList.setItems(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void loadFeatureSection(Map<String, String> features) {
        List<FeatureRow> list = generateRowsFromMap(features);
        ObservableList<FeatureRow> data = FXCollections.observableArrayList(list);
        featureCol.setCellValueFactory(new PropertyValueFactory<>("feature"));
        currentValCol.setCellValueFactory(new PropertyValueFactory<>("oldValue"));
        EdValCol.setCellValueFactory(new PropertyValueFactory<>("newValue"));
        featureTable.setItems(data);
    }

    private List<FeatureRow> generateRowsFromMap(Map<String, String> features) {
        List<FeatureRow> list = new ArrayList<>();
        features.forEach((key, value) -> list.add(new FeatureRow(key, value)));
        return list;
    }

    private void loadMainSection(MiniProductDto pm, Map<String, String> features) {
        SellPackageDto sellPackagePM = pm.findPackageForSeller(CacheData.getInstance().getUsername());
        name.setPromptText(pm.getName());
        String weight = features.getOrDefault("Weigh", "Not Specified");
        String dim = features.getOrDefault("Dimension", "Not Specified");
        features.remove("Weigh");
        features.remove("Color");
        features.remove("Dimension");
        weigh.setPromptText(weight);
        dimension.setPromptText(dim);
        price.setPromptText("" + sellPackagePM.getPrice());
        stock.setPromptText("" + sellPackagePM.getStock());
    }

    public class FeatureRow {
        private String feature;
        private String oldValue;
        private String newValue;

        FeatureRow(String feature, String oldValue) {
            this.feature = feature;
            this.oldValue = oldValue;
            this.newValue = "";
        }

        boolean is(String feature) {
            return this.feature.equals(feature);
        }

        public String getFeature() {
            return feature;
        }

        public void setFeature(String feature) {
            this.feature = feature;
        }

        public String getOldValue() {
            return oldValue;
        }

        public void setOldValue(String oldValue) {
            this.oldValue = oldValue;
        }

        public String getNewValue() {
            return newValue;
        }

        public void setNewValue(String newValue) {
            this.newValue = newValue;
        }
    }
}
