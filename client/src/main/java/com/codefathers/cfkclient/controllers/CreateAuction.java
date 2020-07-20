package com.codefathers.cfkclient.controllers;

import com.codefathers.cfkclient.BackAbleController;
import com.codefathers.cfkclient.dtos.product.MiniProductDto;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;

public class CreateAuction extends BackAbleController {
    public JFXButton back;
    public JFXButton minimize;
    public JFXButton close;

    public DatePicker startDate;
    public DatePicker endDate;
    public JFXButton create;
    public ComboBox<MiniProductDto> products;

    @FXML
    public void initialize(){
        initButts();
        initCombo();
    }

    private void initButts() {

    }

    private void initCombo() {

    }
}
