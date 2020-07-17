package com.codefathers.cfkclient;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;

public class CFK extends Application {
    private static ConfigurableApplicationContext context;

    @Override
    public void init() throws Exception {
        context = SpringApplication.run(CFKClientApplication.class);
    }

    @Override
    public void start(Stage stage) throws Exception {
        context.publishEvent(new StageReadyEvent(stage));
    }

    @Override
    public void stop() throws Exception {
        context.stop();
        Platform.exit();
    }

    public static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(CFK.class.getClassLoader().getResource("./fxmls/" + fxml + ".fxml"));
        fxmlLoader.setControllerFactory(context :: getBean);
        return fxmlLoader.load();
    }

    class StageReadyEvent extends ApplicationEvent{

        public StageReadyEvent(Stage source) {
            super(source);
        }

        public Stage getStage(){
            Stage.class.cast(getSource());
        }
    }
}
