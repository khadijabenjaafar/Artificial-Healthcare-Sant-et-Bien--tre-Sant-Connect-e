package org.example.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class AfficheFacturationBack implements Initializable {

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> filterCombo;

    @FXML
    private ComboBox<String> sortCombo;

    private ObservableList<Facturation> facturationList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            facturationList.addAll(new ServiceFacturation().afficher());
            setupUI();
            afficherFacturations(facturationList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setupUI() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        filterCombo.setItems(FXCollections.observableArrayList("Tous", "Payé", "Non payé"));
        filterCombo.getSelectionModel().selectFirst();
        filterCombo.setOnAction(e -> applyFilters());
        sortCombo.setItems(FXCollections.observableArrayList("Aucun", "Montant croissant", "Montant décroissant", "Date récente", "Date ancienne"));
        sortCombo.getSelectionModel().selectFirst();
        sortCombo.setOnAction(e -> applyFilters());
    }

    private void applyFilters() {
        String keyword = searchField.getText().toLowerCase();
        String filterStatut = filterCombo.getValue();
        String sortOption = sortCombo.getValue();

        List<Facturation> filtered = facturationList.stream()
                .filter(f -> f.getMethodePaiement().toLowerCase().contains(keyword)
                        || f.getStatut().toLowerCase().contains(keyword)
                        || String.valueOf(f.getMontant()).contains(keyword)
                        || f.getDateFacturation().toString().contains(keyword))
                .filter(f -> {
                    if (filterStatut.equals("Payé")) return f.getStatut().equalsIgnoreCase("payé");
                    if (filterStatut.equals("Non payé")) return !f.getStatut().equalsIgnoreCase("payé");
                    return true;
                })
                .collect(Collectors.toList());

        // Tri
        switch (sortOption) {
            case "Montant croissant" -> filtered.sort(Comparator.comparingDouble(Facturation::getMontant));
            case "Montant décroissant" -> filtered.sort(Comparator.comparingDouble(Facturation::getMontant).reversed());
            case "Date récente" -> filtered.sort(Comparator.comparing(Facturation::getDateFacturation).reversed());
            case "Date ancienne" -> filtered.sort(Comparator.comparing(Facturation::getDateFacturation));
        }

        afficherFacturations(FXCollections.observableArrayList(filtered));
    }

    private void afficherFacturations(List<Facturation> facturations) {
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
        title.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 16));
        title.setTextFill(Color.web("#0fb5a7"));

        Label montantLabel = new Label("💰 Montant : " + facturation.getMontant() + " TND");
        Label dateLabel = new Label("📅 Date : " + facturation.getDateFacturation());
        Label methodePLabel = new Label("💳 Méthode : " + facturation.getMethodePaiement());
        Label statutLabel = new Label("📌 Statut : " + facturation.getStatut());

        for (Label label : new Label[]{montantLabel, dateLabel, methodePLabel, statutLabel}) {
            label.setFont(Font.font("Segoe UI", 13));
        }

        Button viewDetailsBtn = new Button("👁 Voir Détails");
        viewDetailsBtn.setStyle("-fx-background-color: #0fb5a7; -fx-text-fill: white; -fx-font-weight: bold;");
        viewDetailsBtn.setOnAction(e -> showDetailsModal(facturation));

        VBox card = new VBox(10, title, montantLabel, dateLabel, methodePLabel, statutLabel, viewDetailsBtn);
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

        // Effet survol
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

    private void showDetailsModal(Facturation facturation) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Détails Facturation");
        alert.setHeaderText(null);
        alert.setContentText(
                "Montant : " + facturation.getMontant() + " TND\n" +
                        "Date : " + facturation.getDateFacturation() + "\n" +
                        "Méthode : " + facturation.getMethodePaiement() + "\n" +
                        "Statut : " + facturation.getStatut()
        );
        alert.showAndWait();
    }
}
