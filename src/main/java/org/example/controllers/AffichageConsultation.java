package org.example.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.example.entities.Consultation;
import org.example.services.ServiceConsultation;

import java.net.URL;
import java.sql.SQLException;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class AffichageConsultation implements Initializable {

    @FXML
    private VBox rootVBox;

    @FXML
    private GridPane grid;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> monthComboBox;

    private final ServiceConsultation service = new ServiceConsultation();
    private List<Consultation> allConsultations;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            allConsultations = service.afficher();
            setupMonthComboBox();
            displayConsultations(allConsultations);

            // Listener pour recherche
            searchField.textProperty().addListener((observable, oldValue, newValue) -> filterConsultations());

            // Listener pour tri par mois
            monthComboBox.valueProperty().addListener((observable, oldValue, newValue) -> filterConsultations());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setupMonthComboBox() {
        List<String> months = Arrays.stream(Month.values())
                .map(Month::name)
                .map(String::toLowerCase)
                .map(m -> m.substring(0,1).toUpperCase() + m.substring(1))
                .collect(Collectors.toList());

        monthComboBox.setItems(FXCollections.observableArrayList(months));
        monthComboBox.getItems().add(0, "Tous les mois");
        monthComboBox.getSelectionModel().selectFirst(); // Par défaut : tous les mois
    }

    private void displayConsultations(List<Consultation> consultations) {
        grid.getChildren().clear();
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
    }

    private void filterConsultations() {
        String keyword = searchField.getText();
        String selectedMonth = monthComboBox.getValue();

        List<Consultation> filtered = allConsultations.stream()
                .filter(c -> {
                    boolean matchesSearch = (keyword == null || keyword.isEmpty()) || (
                            (c.getObservation() != null && c.getObservation().toLowerCase().contains(keyword.toLowerCase())) ||
                                    (c.getDiagnostic() != null && c.getDiagnostic().toLowerCase().contains(keyword.toLowerCase())) ||
                                    (c.getTraitement() != null && c.getTraitement().toLowerCase().contains(keyword.toLowerCase()))
                    );

                    boolean matchesMonth = true;
                    if (selectedMonth != null && !"Tous les mois".equals(selectedMonth)) {
                        if (c.getRendezVous() != null && c.getRendezVous().getDateHeure() != null) {
                            Month month = c.getRendezVous().getDateHeure().getMonth();
                            matchesMonth = month.name().equalsIgnoreCase(selectedMonth);
                        } else {
                            matchesMonth = false;
                        }
                    }

                    return matchesSearch && matchesMonth;
                })
                .collect(Collectors.toList());

        displayConsultations(filtered);
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
