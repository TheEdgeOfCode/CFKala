package com.codefathers.cfkclient.controllers;

import com.codefathers.cfkclient.BackAbleController;
import com.codefathers.cfkclient.CFK;
import com.codefathers.cfkclient.dtos.discount.*;
import com.codefathers.cfkclient.dtos.edit.DiscountCodeEditAttributes;
import com.codefathers.cfkclient.utils.Connector;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.codefathers.cfkclient.controllers.Notification.show;
import static com.codefathers.cfkclient.controllers.SysDis.*;

public class DiscountManager extends BackAbleController {
    @FXML private JFXButton back;
    @FXML private JFXButton refresh;
    @FXML private JFXButton minimize;
    @FXML private JFXButton close;
    @FXML private JFXTextField crCode;
    @FXML private JFXSlider crPercentage;
    @FXML private JFXTextField crMaximum;
    @FXML private DatePicker crStartDate;
    @FXML private TextField crStartH;
    @FXML private TextField crStartM;
    @FXML private TextField crStartS;
    @FXML private DatePicker crEndDate;
    @FXML private TextField crEndH;
    @FXML private TextField crEndM;
    @FXML private TextField crEndS;
    @FXML private JFXButton crButton;
    @FXML private VBox editBox;
    @FXML private JFXSlider edPercent;
    @FXML private JFXTextField edMaximum;
    @FXML private DatePicker edStartDate;
    @FXML private TextField edStartH;
    @FXML private TextField edStartM;
    @FXML private TextField edStartS;
    @FXML private DatePicker edEndDate;
    @FXML private TextField edEndH;
    @FXML private TextField edEndM;
    @FXML private TextField edEndS;
    @FXML private JFXButton edConfirm;
    @FXML private JFXButton edReset;

    @FXML private TableView<UserIntegerPM> userTable;
    @FXML private TableColumn<UserIntegerPM,String> userColumn;
    @FXML private TableColumn<UserIntegerPM,Integer> amountColumn;

    @FXML private JFXButton removeUser;
    @FXML private JFXTextField addUsername;
    @FXML private JFXTextField addUserQuantity;
    @FXML private JFXButton addUser;
    @FXML private VBox sysAddBox;

    @FXML private JFXComboBox<String> sysMode;
    @FXML private JFXTextField sysQuantity;
    @FXML private JFXButton sysAdd;
    @FXML private JFXButton delete;

    @FXML private ListView<DisCodeManagerPM> codes;

    private static final Paint redColor = Paint.valueOf("#c0392b");
    private static final Paint blueColor = Paint.valueOf("#405aa8");
    private static Connector connector = Connector.getInstance();

    @FXML
    public void initialize(){
        loadList();
        listeners();
        binds();
        resetFields();
        upBarInitialize();
        codeCreateBoxInitialize();
        userTableInitialize();
        addUserInitialize();
        editCode();
        systematicAdditionInitialize();
    }

    private void systematicAdditionInitialize() {
        sysAdd.setOnAction(event -> getUsersSystematicDiscount());
    }

    private void getUsersSystematicDiscount() {
        if (sysQuantity.getText().isBlank()) {
            errorField(sysQuantity, "Amount Required");
        } else if (!sysQuantity.getText().matches("\\d{1,9}")) {
            errorField(sysQuantity, "Number Required");
        } else {
            SysDis sysDis = TO10;
            switch (sysMode.getValue()) {
                case "Users Shop More Than 1500$":
                    sysDis = MORE1500; break;
                case "Users Shop More Than 2500$":
                    sysDis = MORE2500;break;
                case "To 10 Random User":
                    sysDis = TO10;break;
                case "To 50 Random User":
                    sysDis = TO50;break;
                case "To 100 Random User":
                    sysDis = TO100;break;
            }
            try {
                connector.systematicDiscount(new CreateDiscountSystematic(codes.getSelectionModel().getSelectedItem().getDiscountCode()
                        , sysDis, Integer.parseInt(sysQuantity.getText())));
            } catch (Exception e) {
                show("Error",e.getMessage(),back.getScene().getWindow(),true);
            }
        }
    }

    private void editCode() {
        edReset.setOnAction(e-> reloadEditBox());
        edConfirm.setOnAction(e->{
            if (checkEditBoxValidity()){
                sendEditRequest();
                reset();
            }
        });
        delete.setOnAction(event -> {
            try {
                connector.removeDiscountCode(codes.getSelectionModel().getSelectedItem().getDiscountCode());
            } catch (Exception e) {
                show("Error", e.getMessage(), back.getScene().getWindow(), true);
            }
        });
    }

    private void sendEditRequest() {
        DiscountCodeEditAttributes attributes = new DiscountCodeEditAttributes();
        if (edStartDate.getValue() != null) {
            Date start = createDate(edStartDate,edStartH,edStartM,edStartS);
            attributes.setStart(start);
        }
        if (edEndDate.getValue() != null){
            Date end = createDate(edEndDate,edEndH,edEndM,edEndS);
            attributes.setEnd(end);
        }
        int percent = (int)edPercent.getValue();
        attributes.setOffPercent(percent);
        if (!edMaximum.getText().isEmpty()){
            int maximum = Integer.parseInt(edMaximum.getText());
            attributes.setMaxDiscount(maximum);
        }
        String code = codes.getSelectionModel().getSelectedItem().getDiscountCode();
        try {
            attributes.setCode(code);
            connector.editDiscountCode(attributes);
        } catch (Exception e) {
            show("Error", e.getMessage(), back.getScene().getWindow(), true);
            e.printStackTrace();
        }
    }

    private Date createDate(DatePicker datePicker, TextField h, TextField m, TextField s) {
        int startH = Integer.parseInt(h.getText());
        int startM = Integer.parseInt(m.getText());
        int startS = Integer.parseInt(s.getText());
        Date start = convertToDateViaInstant(datePicker.getValue());
        start.setHours(startH);
        start.setMinutes(startM);
        start.setSeconds(startS);
        return start;
    }

    private boolean checkEditBoxValidity() {
        if (crPercentage.getValue() == 0){
            show("Error","Percentage Cannot be Zero!!",back.getScene().getWindow(),true);
            return false;
        } else if (!edMaximum.getText().matches("\\d{0,9}")){
            errorField(crMaximum,"Maximum Should Be Numerical");
            return false;
        } else return true;
    }

    private void reloadEditBox() {
        DisCodeManagerPM pm = codes.getSelectionModel().getSelectedItem();
        loadCodeInfo(pm);
    }

    private void resetFields() {
        resetSettingForFields(crCode,"Code");
        resetSettingForFields(crMaximum,"Maximum Of Discount amount");
        resetSettingForFields(edMaximum,"Maximum Of Discount amount");
        resetSettingForFields(addUsername,"Username");
        resetSettingForFields(addUserQuantity,"Quantity");
        resetSettingForFields(sysQuantity,"Quantity");
    }

    private void addUserInitialize() {
        addUser.setOnAction(e -> sendAddUserRequest());
    }

    private void sendAddUserRequest() {
        if (addValuesAreValid()) {
            String user = addUsername.getText();
            String code = codes.getSelectionModel().getSelectedItem().getDiscountCode();
            int amount = Integer.parseInt(addUserQuantity.getText());
            try {
                connector.addUserToDiscountCode(new AddUser(user,code,amount));
                show("Successful", "User Was Added To Your Discount Code!!!\nRefresh To See", back.getScene().getWindow(), false);
            } catch (Exception ex) {
                show("Error", ex.getMessage(), back.getScene().getWindow(), true);
                ex.printStackTrace();
            }
        }
    }

    private boolean addValuesAreValid() {
        if (addUsername.getText().isEmpty()){
            errorField(addUsername,"Username*");
            return false;
        } else if (addUserQuantity.getText().isEmpty()){
            errorField(addUserQuantity,"Amount Required");
            return false;
        } else if (!addUserQuantity.getText().matches("\\d{0,9}")){
            errorField(addUserQuantity,"Numerical!!");
            return false;
        } else return true;
    }

    private void userTableInitialize() {
        removeUser.setOnAction(e-> sendRemoveUserRequest());
    }

    private void sendRemoveUserRequest() {
        String userId = userTable.getSelectionModel().getSelectedItem().getUsername();
        String code = codes.getSelectionModel().getSelectedItem().getDiscountCode();
        try {
            connector.removeUserFromDiscountCodeUsers(code,userId);
            show("Successful", "User Was Removed From Your Discount Code!!!", back.getScene().getWindow(), false);
            reset();
        } catch (Exception e) {
            show("Error", e.getMessage(), back.getScene().getWindow(), true);
        }
    }

    private void codeCreateBoxInitialize() {
        crButton.setOnAction(e->{
            if (checkCreateBoxValidity()){
                sendCreateRequest();
            }
        });
    }

    private void sendCreateRequest() {
        String[] data = {crCode.getText(),
                Integer.toString((int)crPercentage.getValue()),
                crMaximum.getText()};
        int startH = Integer.parseInt(crStartH.getText());
        int startM = Integer.parseInt(crStartM.getText());
        int startS = Integer.parseInt(crStartS.getText());
        int endH = Integer.parseInt(crEndH.getText());
        int endM = Integer.parseInt(crEndM.getText());
        int endS = Integer.parseInt(crEndS.getText());
        Date start = convertToDateViaInstant(crStartDate.getValue());
        Date end = convertToDateViaInstant(crEndDate.getValue());
        start.setHours(startH);
        start.setMinutes(startM);
        start.setSeconds(startS);
        end.setHours(endH);
        end.setMinutes(endM);
        end.setSeconds(endS);
        try {
            connector.createDiscount(new CreateDiscount(data,start,end));
            reset();
        } catch (Exception e) {
            show("Error", e.getMessage(), back.getScene().getWindow(), true);
            e.printStackTrace();
        }
    }

    private Date convertToDateViaInstant(LocalDate dateToConvert) {
        return java.util.Date.from(dateToConvert.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }

    private boolean checkCreateBoxValidity() {
        if (crCode.getText().isEmpty()){
            errorField(crCode,"Code Is Required");
            return false;
        }else if (!crCode.getText().matches("\\w+")){
            errorField(crCode,"Code Can Contain letters & Numbers");
            return false;
        } else if (crPercentage.getValue() == 0){
            show("Error","Percentage Cannot be Zero!!",back.getScene().getWindow(),true);
            return false;
        } else if (isCrStartDateInvalid()){
            show("Error","Wrong Date Format",back.getScene().getWindow(),true);
            return false;
        } else if (crMaximum.getText().isEmpty()){
            errorField(crMaximum,"Maximum Can't Be Empty");
            return false;
        }
        else if (!crMaximum.getText().matches("\\d{0,9}")){
            errorField(crMaximum,"Maximum Should Be Numerical");
            return false;
        } else return true;
    }

    private boolean isCrStartDateInvalid() {
        if (crStartDate.getValue() == null ||
                crEndDate.getValue() == null){
            return true;
        }
        LocalDate startDate = crStartDate.getValue();
        LocalDate endDate = crEndDate.getValue();
        if (startDate.isAfter(endDate)){
            return true;
        } else if (startDate.isEqual(endDate)){
            try{
                int startH = Integer.parseInt(crStartH.getText());
                int startM = Integer.parseInt(crStartM.getText());
                int startS = Integer.parseInt(crStartS.getText());
                int endH = Integer.parseInt(crEndH.getText());
                int endM = Integer.parseInt(crEndM.getText());
                int endS = Integer.parseInt(crEndS.getText());
                Date start = new Date(2020, Calendar.APRIL,12,startH,startM,startS);
                Date end = new Date(2020,Calendar.APRIL,12,endH,endM,endS);
                return start.after(end);
            }catch (Exception e){
                return false;
            }
        } else return false;
    }

    private void errorField(JFXTextField field,String prompt){
        field.setPromptText(prompt);
        field.setFocusColor(redColor);
        field.requestFocus();
    }

    private void resetSettingForFields(JFXTextField field,String prompt){
        field.textProperty().addListener(e->{
            field.setFocusColor(blueColor);
            field.setPromptText(prompt);
        });
    }

    private void upBarInitialize() {
        back.setOnAction(e-> { try {
            Scene scene = new Scene(CFK.loadFXML(back(), backForBackward()));
            CFK.setSceneToStage(back, scene);
        } catch (IOException ignore) {}
        });
        minimize.setOnAction(e -> ((Stage) close.getScene().getWindow()).setIconified(true));
        refresh.setOnAction(e -> reset());
        close.setOnAction(e -> ((Stage) close.getScene().getWindow()).close());
    }

    private void binds() {
        editBox.disableProperty().bind(Bindings.isEmpty(codes.getSelectionModel().getSelectedItems()));
        removeUser.disableProperty().bind(Bindings.isEmpty(userTable.getSelectionModel().getSelectedItems()));
        addUser.disableProperty().bind(Bindings.isEmpty(addUserQuantity.textProperty())
                .or(Bindings.isEmpty(addUsername.textProperty())));
        edReset.disableProperty().bind(edConfirm.disableProperty());
        crButton.disableProperty().bind((crStartDate.valueProperty().isNull()
                .or(Bindings.isEmpty(crStartH.textProperty())
                        .or(Bindings.isEmpty(crStartM.textProperty())
                                .or(Bindings.isEmpty(crStartS.textProperty())))))
                .or((crEndDate.valueProperty().isNull()
                        .or(Bindings.isEmpty(crEndH.textProperty()))
                        .or(Bindings.isEmpty(crEndM.textProperty()))
                        .or(Bindings.isEmpty(crEndS.textProperty()))))
                .or(Bindings.isEmpty(crMaximum.textProperty()))
                .or(Bindings.isEmpty(crCode.textProperty())));
        sysAdd.disableProperty().bind(sysMode.getSelectionModel().selectedItemProperty().isNull());
    }

    private void listeners() {
        codes.getSelectionModel().selectedItemProperty().addListener((v,oldValue,newValue) -> {
            if (newValue != null) {
                loadCodeInfo(newValue);
                loadTableData(newValue);
            }
        });
    }

    private void loadCodeInfo(DisCodeManagerPM pm) {
        edPercent.setValue(pm.getOffPercentage());
        edMaximum.setText("");
        edMaximum.setPromptText("Maximum : " + pm.getMaxOfPriceDiscounted());
        Date startDate = pm.getStartTime();
        Date endDate = pm.getEndTime();
        setDatesToFields(edStartDate,startDate,0);
        edStartDate.setValue(null);
        setDatesToFields(edEndDate,endDate,1);
        edEndDate.setValue(null);
        confirmBindings(pm);
    }

    private void confirmBindings(DisCodeManagerPM pm) {
        edConfirm.disableProperty().bind((edStartDate.valueProperty().isNull()
                .or(Bindings.isEmpty(edStartH.textProperty())
                        .or(Bindings.isEmpty(edStartM.textProperty())
                                .or(Bindings.isEmpty(edStartS.textProperty())))))
                .and((edEndDate.valueProperty().isNull()
                        .or(Bindings.isEmpty(edEndH.textProperty()))
                        .or(Bindings.isEmpty(edEndM.textProperty()))
                        .or(Bindings.isEmpty(edEndS.textProperty()))))
                .and(edPercent.valueProperty().isEqualTo(pm.getOffPercentage()))
                .and(Bindings.isEmpty(edMaximum.textProperty())));
    }

    private void setDatesToFields(DatePicker datePicker, Date date,int mode) {
        datePicker.setPromptText(date.toString());
        switch (mode){
            case 0:
                edStartH.setPromptText(String.format("%02d",date.getHours()));
                edStartM.setPromptText(String.format("%02d",date.getMinutes()));
                edStartS.setPromptText(String.format("%02d",date.getSeconds()));
                break;
            case 1:
                edEndH.setPromptText(String.format("%02d",date.getHours()));
                edEndM.setPromptText(String.format("%02d",date.getMinutes()));
                edEndS.setPromptText(String.format("%02d",date.getSeconds()));
                break;
        }
    }

    private void loadTableData(DisCodeManagerPM pm) {
        ObservableList<UserIntegerPM> data = FXCollections.observableArrayList(pm.getUsers());
        userColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("integer"));
        userTable.setItems(data);
    }

    private void loadList() {
        try {
            ArrayList<DisCodeManagerPM> list = connector.getDiscountCodes();
            ObservableList<DisCodeManagerPM> data = FXCollections.observableArrayList(list);
            codes.setItems(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void reset(){
        userTable.setItems(FXCollections.observableArrayList());
        loadList();
    }
}
