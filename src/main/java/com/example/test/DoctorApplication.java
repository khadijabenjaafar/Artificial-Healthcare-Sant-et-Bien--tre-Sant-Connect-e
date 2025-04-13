package com.example.test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DoctorApplication extends Application {
    @Override
        public void start(Stage stage) throws Exception {
            // Charge le fichier FXML du dashboard avec BorderPane
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/doctor.fxml"));

            Scene scene = new Scene(root);
            stage.setTitle("Dashboard");
            stage.setScene(scene);
            stage.show();
        }

        public static void main(String[] args) {
            launch(args);
        }
    }

