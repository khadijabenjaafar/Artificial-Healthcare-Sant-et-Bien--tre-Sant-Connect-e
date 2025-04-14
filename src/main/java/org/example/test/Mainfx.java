 package org.example.test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.controllers.IndexFront;

import java.io.IOException;

public class Mainfx extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/indexFront.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);

            // 💡 Détection de la touche Échap
            scene.setOnKeyPressed(event -> {
                switch (event.getCode()) {
                    case ESCAPE -> primaryStage.close();
                }
            });

            primaryStage.initStyle(StageStyle.UNDECORATED);
            primaryStage.setScene(scene);
            primaryStage.show();

            IndexFront controller = loader.getController();
            controller.setStage(primaryStage);
        }



    public static void main(String[] args) {
        launch(args); // Lancer l'application JavaFX
    }
}
