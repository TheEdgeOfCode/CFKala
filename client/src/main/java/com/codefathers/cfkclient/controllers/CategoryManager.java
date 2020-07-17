package com.codefathers.cfkclient.controllers;

import com.codefathers.cfkclient.BackAbleController;
import com.codefathers.cfkclient.CFK;
import com.codefathers.cfkclient.dtos.category.CategoryPM;
import com.codefathers.cfkclient.dtos.category.CreateDTO;
import com.codefathers.cfkclient.dtos.edit.CategoryEditAttribute;
import com.codefathers.cfkclient.utils.Connector;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CategoryManager extends BackAbleController {
    @FXML private JFXButton back;
    @FXML private JFXButton minimize;
    @FXML private JFXButton close;
    @FXML private TextField categoryName;
    @FXML private JFXButton editName;
    @FXML private JFXButton deleteCategory;
    @FXML private ListView<String> featureList;
    @FXML private TextField newFeature;
    @FXML private Button newFeatureButt;
    @FXML private Button removeFeatureButt;
    @FXML private JFXTextField crName;
    @FXML private ComboBox<CategoryPM> crParent;
    @FXML private ListView<String> featureListCr;
    @FXML private TextField featureInput;
    @FXML private Button addFeatureCr;
    @FXML private JFXButton createCategory;
    @FXML private ListView<CategoryPM> categories;

    private static Connector connector = Connector.getInstance();

    @FXML
    public void initialize() {
        loadList();
        listeners();
        bindings();
        buttonInitialize();
    }

    private void buttonInitialize() {
        back.setOnAction(event -> {
            try {
                Scene scene = new Scene(CFK.loadFXML(back(), backForBackward()));
                CFK.setSceneToStage(back, scene);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        minimize.setOnAction(event -> CFK.minimize());
        close.setOnAction(event -> CFK.close());
        editName.setOnAction(event -> sendEditNameRequest());
        addFeatureCr.setOnAction(event -> addFeatureToCreationList());
        newFeatureButt.setOnAction(event -> sendNewFeatureRequest());
        removeFeatureButt.setOnAction(event -> sendRemoveFeatureRequest());
        createCategory.setOnAction(event -> {
            if (checkValidValues()) {
                sendCategoryCreationRequest();
            }
        });
        deleteCategory.setOnAction(event -> deleteCategoryAction());
    }

    private void deleteCategoryAction() {
        int id = categories.getSelectionModel().getSelectedItem().getId();
        try {
            connector.removeCategory(id);
        } catch (Exception e) {
            Notification.show("Error", e.getMessage(), back.getScene().getWindow(), true);
        }
    }

    private void sendCategoryCreationRequest() {
        String name = crName.getText();
        int parentId = crParent.getSelectionModel().getSelectedItem().getId();
        List<String> features = new ArrayList<>(featureListCr.getItems());
        try {
            connector.addCategory(new CreateDTO(name, parentId, features));
            reset();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkValidValues() {
        if (crName.getText().isEmpty()) {
            crName.setPromptText("Name Is Required");
            crName.setFocusColor(Paint.valueOf("#c0392b"));
            crName.requestFocus();
            return false;
        }
        return true;
    }

    private void addFeatureToCreationList() {
        String feature = featureInput.getText();
        featureListCr.getItems().add(feature);
        featureInput.setText("");
    }

    private void sendRemoveFeatureRequest() {
        String featureToRemove = featureList.getSelectionModel().getSelectedItem();
        int id = categories.getSelectionModel().getSelectedItem().getId();
        CategoryEditAttribute attribute = new CategoryEditAttribute();
        attribute.setRemoveFeature(featureToRemove);
        try {
            attribute.setSourceId(id);
            connector.editCategory(attribute);
            featureList.getItems().removeAll(featureToRemove);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendNewFeatureRequest() {
        String newFeatureTitle = newFeature.getText();
        CategoryEditAttribute attribute = new CategoryEditAttribute();
        attribute.setAddFeature(newFeatureTitle);
        int id = categories.getSelectionModel().getSelectedItem().getId();
        try {
            attribute.setSourceId(id);
            connector.editCategory(attribute);
            featureList.getItems().add(newFeatureTitle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendEditNameRequest() {
        String newName = categoryName.getText();
        CategoryEditAttribute attribute = new CategoryEditAttribute();
        attribute.setName(newName);
        int id = categories.getSelectionModel().getSelectedItem().getId();
        try {
            attribute.setSourceId(id);
            connector.editCategory(attribute);
            categoryName.setPromptText(newName);
            reset();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bindings() {
        removeFeatureButt.disableProperty().bind(Bindings.isEmpty(featureList.getSelectionModel().getSelectedItems()));
        newFeatureButt.disableProperty().bind(Bindings.isEmpty(newFeature.textProperty()));
        addFeatureCr.disableProperty().bind(Bindings.isEmpty(featureInput.textProperty()));
        editName.disableProperty().bind(Bindings.isEmpty(categoryName.textProperty())
                .or(Bindings.isEmpty(categories.getSelectionModel().getSelectedItems())));
        createCategory.disableProperty().bind(crParent.valueProperty().isNull().or(Bindings.isEmpty(featureListCr.getItems())));
    }

    private void listeners() {
        categories.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                categoryName.setPromptText(newValue.getName());
                fillFeatureTable(newValue.getId());
            }
        });
        featureInput.setOnAction(event -> addFeatureToCreationList());
    }

    private void fillFeatureTable(int id) {
        try {
            ArrayList<String> list = connector.getSpecialFeatureOfCategory(id);
            ObservableList<String> data = FXCollections.observableArrayList(list);
            featureList.setItems(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadList() {
        ArrayList<CategoryPM> categoryPMS;
        try {
            categoryPMS = connector.getAllCategories();
            ObservableList<CategoryPM> data = FXCollections.observableArrayList(categoryPMS);
            categoryPMS.add(0, new CategoryPM("---", 0, 0));
            ObservableList<CategoryPM> data1 = FXCollections.observableArrayList(categoryPMS);

            crParent.setItems(data1);
            categories.setItems(data);
        } catch (Exception ignore) {}
    }

    private void reset() {
        categories.getItems().clear();
        featureList.getItems().clear();
        featureListCr.getItems().clear();
        categoryName.setPromptText("");
        loadList();
    }
}
