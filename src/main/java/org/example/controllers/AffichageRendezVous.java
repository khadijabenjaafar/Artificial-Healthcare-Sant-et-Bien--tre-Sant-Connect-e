package org.example.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
import java.util.stream.Collectors;

public class AffichageRendezVous implements Initializable {

    @FXML
    private VBox rootVBox; // Reference to the root VBox in FXML

    @FXML
    private GridPane grid; // Reference to the GridPane in FXML
    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> sortComboBox;
    private final ServiceRendezVous service = new ServiceRendezVous();
    private ObservableList<RendezVous> allRendezVous;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Charger tous les rendez-vous
            List<RendezVous> rendezVousList = service.afficher();
            allRendezVous = FXCollections.observableArrayList(rendezVousList);

            // Initialiser la grille
            updateGrid(rendezVousList);

            // Ajouter un écouteur sur le champ de recherche
            searchField.textProperty().addListener((observable, oldValue, newValue) -> filterAndSort());

            // Ajouter un écouteur sur le combo-box pour le tri
            sortComboBox.valueProperty().addListener((observable, oldValue, newValue) -> filterAndSort());

            // Initialiser les options du tri
            sortComboBox.getItems().addAll("Date croissante", "Date décroissante");
            sortComboBox.setValue("Date croissante");  // Valeur par défaut

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void filterAndSort() {
        // Récupérer la valeur du champ de recherche
        String searchQuery = searchField.getText().toLowerCase();
        String sortOption = sortComboBox.getValue();

        // Filtrer la liste des rendez-vous en fonction du champ de recherche
        List<RendezVous> filteredList = allRendezVous.stream()
                .filter(rv -> rv.getMotif().name().toLowerCase().contains(searchQuery) || rv.getMode().name().toLowerCase().contains(searchQuery))
                .collect(Collectors.toList());

        // Trier la liste
        if ("Date croissante".equals(sortOption)) {
            filteredList.sort((rv1, rv2) -> rv1.getDateHeure().compareTo(rv2.getDateHeure()));
        } else if ("Date décroissante".equals(sortOption)) {
            filteredList.sort((rv1, rv2) -> rv2.getDateHeure().compareTo(rv1.getDateHeure()));
        }

        // Mettre à jour la grille avec les éléments filtrés et triés
        updateGrid(filteredList);
    }

    private void updateGrid(List<RendezVous> rendezVousList) {
        grid.getChildren().clear();  // Effacer la grille avant de la remplir avec les nouveaux résultats
        int column = 0;
        int row = 0;

        // Ajouter les cartes à la grille
        for (RendezVous rv : rendezVousList) {
            VBox card = createCard(rv);
            grid.add(card, column, row);

            column++;
            if (column == 3) {
                column = 0;
                row++;
            }
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
