package org.example.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.entities.Consultation;
import org.example.entities.RendezVous;
import org.example.services.ServiceConsultation;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class CardConsultation implements Initializable {

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
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));

        // Vérifier si la consultation est expirée (prochain rendez-vous avant aujourd'hui)
        boolean isExpired = c.getProchainRdv() != null && c.getProchainRdv().isBefore(java.time.LocalDate.now());

        // Style de la carte
        String cardStyle = "-fx-background-color: white; " +
                "-fx-border-color: #dddddd; " +
                "-fx-border-radius: 10; " +
                "-fx-background-radius: 10; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 4);";
        if (isExpired) {
            cardStyle = "-fx-background-color: #FFCDD2; " +  // Rouge clair pour consultation expirée
                    "-fx-border-color: #dddddd; " +
                    "-fx-border-radius: 10; " +
                    "-fx-background-radius: 10; " +
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 4);";
        }
        card.setStyle(cardStyle);

        // Style commun pour les labels
        String labelStyle = "-fx-font-size: 16px; -fx-text-fill: #333333;";

        // Créer les labels
        Label labelDate = new Label("Date : " + (c.getRendezVous() != null ? c.getRendezVous().getDateHeure().toLocalDate() : "N/A"));
        labelDate.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #333333;");

        Label labelObservation = new Label("Observation : " + (c.getObservation() != null ? c.getObservation() : "N/A"));
        labelObservation.setStyle(labelStyle);

        Label labelPrix = new Label("Prix : " + c.getPrix());
        labelPrix.setStyle(labelStyle);

        Label labelDuree = new Label("Durée : " + c.getDuree());
        labelDuree.setStyle(labelStyle);

        Label labelProchainRdv = new Label("Prochain RDV : " + (c.getProchainRdv() != null ? c.getProchainRdv() : "N/A"));
        labelProchainRdv.setStyle(labelStyle);

        Label labelDiagnostic = new Label("Diagnostic : " + (c.getDiagnostic() != null ? c.getDiagnostic() : "N/A"));
        labelDiagnostic.setStyle(labelStyle);

        Label labelTraitement = new Label("Traitement : " + (c.getTraitement() != null ? c.getTraitement() : "N/A"));
        labelTraitement.setStyle(labelStyle);

        // Créer les boutons
        Button btnModifier = new Button("Modifier");
        btnModifier.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-border-radius: 5; -fx-padding: 5px 10px;");
        btnModifier.setOnAction(e -> modifierConsultation(c));

        Button btnSupprimer = new Button("Supprimer");
        btnSupprimer.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-border-radius: 5; -fx-padding: 5px 10px;");
        btnSupprimer.setOnAction(e -> supprimerRendezVous(c, card));

        // Mettre les boutons côte à côte
        HBox buttonsBox = new HBox(10); // 10px d'espacement
        buttonsBox.getChildren().addAll(btnModifier, btnSupprimer);

        // Ajouter tous les éléments à la carte
        card.getChildren().addAll(labelDate, labelObservation, labelPrix, labelDuree, labelProchainRdv, labelDiagnostic, labelTraitement, buttonsBox);

        return card;
    }
    private void supprimerRendezVous(Consultation c, VBox card) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Voulez-vous vraiment supprimer cette consultation ?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response.getButtonData().isDefaultButton()) {
                try {
                    service.supprimer(c.getId());

                    Alert info = new Alert(Alert.AlertType.INFORMATION);
                    info.setTitle("Succès");
                    info.setHeaderText(null);
                    info.setContentText("Consultation supprimé avec succès !");
                    info.showAndWait();

                    grid.getChildren().remove(card);

                } catch (SQLException e) {
                    e.printStackTrace();
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setTitle("Erreur");
                    error.setHeaderText(null);
                    error.setContentText("Erreur lors de la suppression !");
                    error.showAndWait();
                }
            }
        });
    }
    private void modifierConsultation(Consultation c) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierConsultation.fxml"));
            Parent root = loader.load();

            // Passer la consultation au contrôleur
            ModifierConsultation controller = loader.getController();
            controller.setConsultation(c); // méthode à créer dans le contrôleur
            controller.setParentController(this); // 🔁 On passe le parent ici

            Stage stage = new Stage();
            stage.setTitle("Modifier Consultation");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void rafraichirAffichage() {
        try {
            grid.getChildren().clear(); // Vide le contenu du GridPane
            List<Consultation> consultations = service.afficher(); // Recharge les données
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
    @FXML
    void AjouterC(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterConsultation.fxml"));
            Parent root = loader.load();

            // Récupérer la fenêtre actuelle et changer la scène
            Stage stage = (Stage) grid.getScene().getWindow(); // Assurez-vous que 'nom' est un contrôle valide
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
