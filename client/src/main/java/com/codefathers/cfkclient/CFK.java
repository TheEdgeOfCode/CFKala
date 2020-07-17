package com.codefathers.cfkclient;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;

public class CFK extends Application {
    private static ConfigurableApplicationContext context;
    private static Stage window;
    private static Scene scene;
    private static double xOffset;
    private static double yOffset;

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

    public static void moveSceneOnMouse(Scene scene, Stage stage) {
        scene.setOnMousePressed(e -> {
            xOffset = e.getSceneX();
            yOffset = e.getSceneY();
        });
        scene.setOnMouseDragged(e -> {
            stage.setX(e.getScreenX() - xOffset);
            stage.setY(e.getScreenY() - yOffset);
        });
    }

    public static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(CFK.class.getClassLoader().getResource("./fxmls/" + fxml + ".fxml"));
        fxmlLoader.setControllerFactory(context :: getBean);
        return fxmlLoader.load();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
        window.setScene(scene);
    }

    public static void setSceneToStage(Node node, Scene scene) {
        Stage stage = (Stage) node.getScene().getWindow();
        moveSceneOnMouse(scene, stage);
        stage.setScene(scene);
    }

    public static void setSceneToStage(Stage stage, Scene scene) {
        moveSceneOnMouse(scene, stage);
        stage.setScene(scene);
        stage.show();
    }

    class StageReadyEvent extends ApplicationEvent{

        public StageReadyEvent(Stage source) {
            super(source);
        }

        public Stage getStage(){
            return Stage.class.cast(getSource());
        }
    }
}