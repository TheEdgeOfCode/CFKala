package com.codefathers.cfkclient.controllers;

import com.codefathers.cfkclient.BackAbleController;
import com.codefathers.cfkclient.CFK;
import com.codefathers.cfkclient.dtos.product.Doc;
import com.codefathers.cfkclient.utils.Connector;
import com.jfoenix.controls.JFXButton;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.springframework.core.io.ByteArrayResource;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DownloadCenter extends BackAbleController {
    public JFXButton back;
    public JFXButton minimize;
    public JFXButton close;

    public TableView<Doc> table;
    public TableColumn<Doc,Integer> id;
    public TableColumn<Doc,String> name;
    public TableColumn<Doc,String> extension;
    public TableColumn<Doc,Long> size;

    public Button download;

    private static Connector connector = Connector.getInstance();

    @FXML
    private void initialize(){
        initButtons();
        loadPurchases();
        initBinds();
    }

    private void initBinds() {
        download.disableProperty().bind(Bindings.isEmpty(table.getSelectionModel().getSelectedItems()));
    }

    private void loadPurchases() {
        try {
            ArrayList<Doc> docs = connector.getPurchasedDocs();
            ObservableList<Doc> data = FXCollections.observableArrayList(docs);
            id.setCellValueFactory(new PropertyValueFactory<>("id"));
            name.setCellValueFactory(new PropertyValueFactory<>("name"));
            extension.setCellValueFactory(new PropertyValueFactory<>("extension"));
            size.setCellValueFactory(new PropertyValueFactory<>("size"));
            table.setItems(data);
        } catch (Exception e) {
            new OopsAlert().show(e.getMessage());
        }
    }

    private void initButtons() {
        back.setOnAction(event -> handleBack());
        minimize.setOnAction(event -> handleMinimize());
        close.setOnAction(event -> handleClose());
        download.setOnAction(event -> handleDownload());
    }

    private void handleDownload() {
        Doc selectedItem = table.getSelectionModel().getSelectedItem();
        int id= selectedItem.getId();
        try {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File directory = directoryChooser.showDialog(null);
            if (directory != null) {
                ByteArrayResource resource = connector.getDoc(id);
                String name = selectedItem.getName();
                String extension = selectedItem.getExtension();
                File file = new File(directory.getPath() + String.format("/%s.%s",name,extension));
                file.createNewFile();
                OutputStream outputStream = new FileOutputStream(file);
                outputStream.write(resource.getByteArray());
                outputStream.close();
            }
        } catch (Exception e) {
            Notification.show("Error",e.getMessage(),back.getScene().getWindow(),true);
            e.printStackTrace();
        }
    }

    private void handleClose() {
        ((Stage)back.getScene().getWindow()).close();
    }

    private void handleMinimize() {
        ((Stage)back.getScene().getWindow()).setIconified(true);
    }

    private void handleBack() {
        try {
            Scene scene = new Scene(CFK.loadFXML(back(),backForBackward()));
            CFK.setSceneToStage(back,scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
