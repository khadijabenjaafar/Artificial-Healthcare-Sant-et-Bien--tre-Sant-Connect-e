package org.example.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.example.entities.Facturation;
import org.example.services.ServiceFacturation;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class AfficheFacturationBack implements Initializable {

    @FXML
    private ScrollPane scrollPane;
    private Stage modalStage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            List<Facturation> facturations = new ServiceFacturation().afficher();
            afficherCarousel(facturations);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void afficherCarousel(List<Facturation> facturations) {
        HBox hbox = new HBox();
        hbox.setSpacing(25);
        hbox.setPadding(new Insets(30));
        hbox.setAlignment(Pos.CENTER_LEFT);

        for (Facturation facturation : facturations) {
            VBox card = createFacturationCard(facturation);
            hbox.getChildren().add(card);
        }

        scrollPane.setContent(hbox);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setPannable(true);
    }

    private VBox createFacturationCard(Facturation facturation) {
        Label title = new Label("Facturation");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        title.setTextFill(Color.web("#0fb5a7"));

        Label montantLabel = new Label("💰 Montant : " + facturation.getMontant() + " TND");
        Label dateLabel = new Label("📅 Date : " + facturation.getDateFacturation());
        Label methodePLabel = new Label("💳 Méthode : " + facturation.getMethodePaiement());
        Label statutLabel = new Label("📌 Statut : " + facturation.getStatut());

        for (Label label : new Label[]{montantLabel, dateLabel, methodePLabel, statutLabel}) {
            label.setFont(Font.font("Arial", 13));
        }


        VBox card = new VBox(10, title, montantLabel, dateLabel, methodePLabel, statutLabel);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(15));
        card.setPrefWidth(250);
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #15d5bc;" +
                        "-fx-border-radius: 15;" +
                        "-fx-background-radius: 15;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 10, 0.1, 0, 2);"
        );

        card.setOnMouseEntered(e -> card.setStyle(
                "-fx-background-color: #f7fdfd;" +
                        "-fx-border-color: #0fb5a7;" +
                        "-fx-border-radius: 15;" +
                        "-fx-background-radius: 15;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 12, 0.2, 0, 4);"
        ));
        card.setOnMouseExited(e -> card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #15d5bc;" +
                        "-fx-border-radius: 15;" +
                        "-fx-background-radius: 15;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 10, 0.1, 0, 2);"
        ));

        return card;
    }

}
