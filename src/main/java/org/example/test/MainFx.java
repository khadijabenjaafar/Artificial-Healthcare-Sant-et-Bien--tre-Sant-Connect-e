package org.example.test;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.example.controllers.CardConsultation;
import org.example.controllers.CardRendezVous;
import org.example.entities.Consultation;
import org.example.entities.RendezVous;
import org.example.services.ServiceConsultation;
import org.example.services.ServiceRendezVous;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;


public class MainFx extends Application {
    @FXML
    private VBox root;


    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterRendezVous.fxml"));
        VBox root = loader.load();

        // Créer la scène
        Scene scene = new Scene(root, 700, 600);
        primaryStage.setTitle("Prendre un rendez-vous");
        primaryStage.setScene(scene);
        primaryStage.show();
        /*VBox root = new VBox();
        root.setSpacing(20);
        root.setStyle("-fx-padding: 20; -fx-background-color: #f0f0f0;");

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);

        try {
            List<RendezVous> rendezVousList = new ServiceRendezVous().afficher();
            int column = 0;
            int row = 0;

            for (RendezVous rv : rendezVousList) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/CardRendezVous.fxml"));
                Region card = fxmlLoader.load();


                CardRendezVous controller = fxmlLoader.getController();
                controller.setData(rv);

                // For responsive sizing
                card.setPrefWidth(300);
                card.setPrefHeight(Region.USE_COMPUTED_SIZE);

                grid.add(card, column, row);

                column++;
                if (column == 3) {
                    column = 0;
                    row++;
                }
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

        root.getChildren().add(grid);

        Scene scene = new Scene(root, 1000, 600);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        primaryStage.setTitle("Liste des Rendez-vous");
        primaryStage.setScene(scene);
        primaryStage.show();*/

// Charger le fichier FXML
        /*FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainView.fxml"));
        BorderPane root = loader.load();

        // Créer la scène avec la racine et l'afficher
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Application Menu");
        primaryStage.show();*/
// Charge le fichier FXML contenant l'interface utilisateur
       /* FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterConsultation.fxml"));
        VBox root = loader.load(); // Charger le fichier FXML dans un VBox
        Scene scene = new Scene(root, 700, 600);
        primaryStage.setScene(scene);
        primaryStage.show();*/
        // Charger le fichier FXML de base
        // Créer une instance de ServiceConsultation
        /*ServiceConsultation serviceConsultation = new ServiceConsultation();

        // Charger le fichier FXML de base
        VBox root = new VBox();  // Conteneur pour afficher les cartes de consultations
        root.setSpacing(10);

        // Récupérer toutes les consultations depuis la base de données
        List<Consultation> consultations = serviceConsultation.afficher();

        // Ajouter chaque consultation dans un CardConsultation
        for (Consultation consultation : consultations) {
            // Créer un FXMLLoader pour chaque card de consultation
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CardConsultation.fxml"));
            AnchorPane card = loader.load();

            // Initialiser le contrôleur avec la consultation
            CardConsultation controller = loader.getController();
            controller.initialize(consultation);

            // Ajouter la carte de consultation à la VBox
            root.getChildren().add(card);
        }

        // Créer la scène et configurer la fenêtre principale
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Consultations");
        primaryStage.setScene(scene);
        primaryStage.show();*/


    }

    public static void main(String[] args) {
        launch(args);
    }

}