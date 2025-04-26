package org.example.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.entities.Ordonnance;
import org.example.services.ServiceOrdonnance;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class AfficheOrdonnanceBack implements Initializable {

    @FXML
    private ScrollPane scrollPane;
    private Stage modalStage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            List<Ordonnance> ordonnances = new ServiceOrdonnance().recuperer();
            afficherCarousel(ordonnances);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void afficherCarousel(List<Ordonnance> ordonnances) {
        HBox hbox = new HBox();
        hbox.setSpacing(20);
        hbox.setPadding(new Insets(20));
        hbox.setAlignment(Pos.CENTER_LEFT);

        for (Ordonnance ordonnance : ordonnances) {
            VBox card = createOrdonnanceCard(ordonnance);
            hbox.getChildren().add(card);
        }

        scrollPane.setContent(hbox);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setPannable(true);
    }

    private VBox createOrdonnanceCard(Ordonnance ordonnance) {
        // Format personnalisé pour la date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Convertir la LocalDate en String avec le format désiré
        Label dateLabel = new Label("Date : " + ordonnance.getDate().format(formatter));
        dateLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Label medicamentsLabel = new Label("Médicaments : " + ordonnance.getMedicaments());
        medicamentsLabel.setWrapText(true);

        Label commentaireLabel = new Label("Commentaire : " + ordonnance.getCommantaire());
        commentaireLabel.setWrapText(true);


        VBox card = new VBox(10, dateLabel, medicamentsLabel, commentaireLabel);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(10));
        card.setPrefWidth(200);
        card.setStyle("-fx-background-color: white; -fx-border-color: #15d5bc; -fx-border-radius: 10; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0.1, 0, 0);");

        return card;
    }

}
