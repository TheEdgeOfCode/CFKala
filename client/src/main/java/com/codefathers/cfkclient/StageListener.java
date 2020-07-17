package com.codefathers.cfkclient;

import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;

@Component
public class StageListener implements ApplicationListener<CFK.StageReadyEvent> {
    private final String applicationTitle;
    private final Resource fxml;
    private final ApplicationContext applicationContext;

    public StageListener(
            @Value("${spring.application.title}") String applicationTitle
            , @Value("classpath:main.fxml") Resource resource,
            ApplicationContext applicationContext) {
        this.applicationTitle = applicationTitle;
        this.fxml = resource;
        this.applicationContext = applicationContext;
    }


    @Override
    public void onApplicationEvent(CFK.StageReadyEvent stageReadyEvent) {
        Stage stage = stageReadyEvent.getStage();
        try {
            URL url = fxml.getURL();
            FXMLLoader loader = new FXMLLoader(url);
            loader.setControllerFactory(applicationContext::getBean);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
