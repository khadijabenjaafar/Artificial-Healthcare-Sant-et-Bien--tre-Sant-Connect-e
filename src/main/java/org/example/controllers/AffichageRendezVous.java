package org.example.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.example.entities.RendezVous;
import org.example.services.ServiceRendezVous;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class AffichageRendezVous implements Initializable {

    @FXML
    private VBox rootVBox; // Reference to the root VBox in FXML

    @FXML
    private GridPane grid; // Reference to the GridPane in FXML

    private final ServiceRendezVous service = new ServiceRendezVous();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            List<RendezVous> rendezVousList = service.afficher();
            int column = 0;
            int row = 0;

            for (RendezVous rv : rendezVousList) {
                VBox card = createCard(rv);
                grid.add(card, column, row);

                column++;
                if (column == 3) {
                    column = 0;
                    row++;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private VBox createCard(RendezVous rv) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-border-color: #dddddd; -fx-border-radius: 10; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 4)");

        // Date format for the label
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        // Create labels for each piece of information
        Label labelDate = new Label("Date : " + rv.getDateHeure().format(formatter));
        labelDate.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        Label labelMotif = new Label("Motif : " + rv.getMotif());
        Label labelStatut = new Label("Statut : " + rv.getStatut());
        Label labelMode = new Label("Mode : " + rv.getMode());
        Label labelMedecin = new Label("Médecin : " + (rv.getMedecin() != null ? rv.getMedecin().getNom() : "Inconnu"));
        Label labelPatient = new Label("Patient : " + (rv.getPatient() != null ? rv.getPatient().getNom() : "Inconnu"));
        Label labelCommentaire = new Label("Commentaire : " + rv.getCommentaire());

        // Add all the labels to the card
        card.getChildren().addAll(labelDate, labelMotif, labelStatut, labelMode, labelMedecin, labelPatient, labelCommentaire);

        return card;
    }
}
