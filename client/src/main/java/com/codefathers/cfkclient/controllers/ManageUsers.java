package com.codefathers.cfkclient.controllers;

import com.codefathers.cfkclient.BackAbleController;
import com.codefathers.cfkclient.CFK;
import com.codefathers.cfkclient.dtos.user.UserFullDTO;
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
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

public class ManageUsers extends BackAbleController {
    @FXML
    private JFXButton back;
    @FXML private JFXButton minimize;
    @FXML private JFXButton close;
    @FXML private TableView<UserFullDTO> table;
    @FXML private TableColumn<UserFullDTO, String> usernameCol;
    @FXML private TableColumn<UserFullDTO, String> roleCol;
    @FXML private ImageView userImage;
    @FXML private Label username;
    @FXML private Label role;
    @FXML private Label lastname;
    @FXML private Label firstname;
    @FXML private Label email;
    @FXML private Label phone;
    @FXML private JFXButton deleteBtn;

    private final Connector connector = Connector.getInstance();

    @FXML
    public void initialize() {
        initButtons();
        initUsersTable();
    }

    private void initButtons() {
        minimize.setOnAction(e -> minimize());
        close.setOnAction(e -> close());
        back.setOnAction(event -> handleBack());
        deleteBtn.setOnAction(event -> handleDeleteUser());
        deleteBtn.disableProperty().bind(Bindings.isEmpty(table.getSelectionModel().getSelectedItems()));
    }

    private void initUsersTable() {
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));

        table.setItems(getUsers());

        table.getSelectionModel().selectedItemProperty().addListener( (v, oldUser, newUser) -> changeData(newUser));
    }

    private ObservableList<UserFullDTO> getUsers() {
        ObservableList<UserFullDTO> users = FXCollections.observableArrayList();
        try {
            users.addAll(connector.showUsers());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    private void changeData(UserFullDTO newUser) {
        username.setText(newUser.getUsername());
        role.setText(newUser.getRole());
        firstname.setText(newUser.getFirstName());
        lastname.setText(newUser.getLastName());
        email.setText(newUser.getEmail());
        phone.setText(newUser.getPhoneNumber());
        try {
            userImage.setImage(connector.userImage());
        } catch (Exception e) {
            Notification.show("Error", "Unable to set image!", back.getScene().getWindow(), true);
        }
    }

    private void handleDeleteUser() {
        UserFullDTO user = table.getSelectionModel().getSelectedItem();
        try {
            connector.deleteUser(user.getUsername());
        } catch (Exception e) {
            e.printStackTrace();
        }

        ObservableList<UserFullDTO> users = table.getItems();
        users.remove(user);

        Notification.show("Successful", "User was Deleted Successfully!!!", back.getScene().getWindow(), false);
    }

    private void handleBack() {
        try {
            Scene scene = new Scene(CFK.loadFXML(back(), backForBackward()));
            CFK.setSceneToStage(back, scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void close() {
        Stage stage = (Stage) close.getScene().getWindow();
        stage.close();
    }

    private void minimize() {
        Stage stage = (Stage) minimize.getScene().getWindow();
        stage.setIconified(true);
    }

}
