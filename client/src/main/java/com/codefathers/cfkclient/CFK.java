package com.codefathers.cfkclient;

import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;

public class CFK extends Application {

    @Override
    public void init() throws Exception {
        SpringApplication.run(CFKClientApplication.class);
    }

    @Override
    public void start(Stage stage) throws Exception {

    }
}
