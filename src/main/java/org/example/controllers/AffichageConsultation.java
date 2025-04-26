package org.example.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.example.entities.Consultation;
import org.example.services.ServiceConsultation;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class AffichageConsultation implements Initializable {

    @FXML
    private VBox rootVBox;

    @FXML
    private GridPane grid;

    private final ServiceConsultation service = new ServiceConsultation();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            List<Consultation> consultations = service.afficher();
            int column = 0;
            int row = 0;

            for (Consultation c : consultations) {
                VBox card = createCard(c);
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

    private VBox createCard(Consultation c) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-border-color: #dddddd; -fx-border-radius: 10; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 4)");

        // Labels for consultation details
        Label labelDate = new Label("Date : " + (c.getRendezVous() != null ? c.getRendezVous().getDateHeure().toLocalDate() : "N/A"));
        labelDate.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        Label labelObservation = new Label("Observation : " + (c.getObservation() != null ? c.getObservation() : "N/A"));
        Label labelPrix = new Label("Prix : " + c.getPrix());
        Label labelDuree = new Label("Durée : " + c.getDuree());
        Label labelProchainRdv = new Label("Prochain RDV : " + c.getProchainRdv());
        Label labelDiagnostic = new Label("Diagnostic : " + (c.getDiagnostic() != null ? c.getDiagnostic() : "N/A"));
        Label labelTraitement = new Label("Traitement : " + (c.getTraitement() != null ? c.getTraitement() : "N/A"));

        // Add all labels to the card
        card.getChildren().addAll(labelDate, labelObservation, labelPrix, labelDuree, labelProchainRdv, labelDiagnostic, labelTraitement);

        return card;
    }
}
