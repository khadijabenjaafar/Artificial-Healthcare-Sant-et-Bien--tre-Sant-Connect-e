 package org.example.test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Mainfx extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {

        // ✅ TEST ACTIF : Afficher la liste des facturations en vue carousel
        Parent root = FXMLLoader.load(getClass().getResource("/AfficheFacturation.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setTitle("Liste des Facturations");
        primaryStage.setScene(scene);
        primaryStage.show();

        /*
        // ❌ TEST INACTIF : Ajouter une Facturation
        Parent root = FXMLLoader.load(getClass().getResource("/AjouterFacturation.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setTitle("Ajouter une Facturation");
        primaryStage.setScene(scene);
        primaryStage.show();
        */

        /*
        // ❌ TEST INACTIF : Ajouter une Ordonnance
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjoutOrdonnance.fxml"));
        Parent ordonnanceRoot = loader.load();
        Scene ordonnanceScene = new Scene(ordonnanceRoot);
        primaryStage.setTitle("Gestion des Ordonnances");
        primaryStage.setScene(ordonnanceScene);
        primaryStage.show();
        */

        /*
        // ❌ TEST INACTIF : Afficher les Ordonnances (vue en carousel)
        Parent root = FXMLLoader.load(getClass().getResource("/AfficheOrdonnance.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setTitle("Ordonnances - Vue en Carousel");
        primaryStage.setScene(scene);
        primaryStage.show();
        */
    }

    public static void main(String[] args) {
        launch(args); // Lancer l'application JavaFX
    }
}
