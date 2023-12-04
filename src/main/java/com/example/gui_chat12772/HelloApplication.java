package com.example.gui_chat12772;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 500, 400);
        stage.setOnCloseRequest(event->{
            System.exit(0);
        });
        stage.setTitle("Сетевой чат");
        stage.setScene(scene);
        stage.show();
        HelloController helloController = fxmlLoader.getController();
        helloController.onConnect();
    }

    public static void main(String[] args) {
        launch();
    }
}