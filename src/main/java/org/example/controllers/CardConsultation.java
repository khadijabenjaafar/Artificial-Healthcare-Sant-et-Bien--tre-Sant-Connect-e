package org.example.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.example.entities.Consultation;
import org.example.entities.RendezVous;
import org.example.services.ServiceConsultation;

import javafx.scene.control.Label;  // Correct
import org.example.services.ServiceRendezVous;

import java.io.IOException;
import java.sql.SQLException;


public class CardConsultation {
    @FXML
    private Label labelDate;
    @FXML
    private Label labelCommentaire;
    @FXML
    private Label labelPrix;
    @FXML
    private Label labelDuree;
    @FXML
    private Label labelProchainRdv;
    @FXML
    private Button btnModifier;
    @FXML
    private Button btnSupprimer;
    private Consultation consultation;
    private ServiceConsultation serviceConsultation;
    @FXML
    private Pane rootPane;

    public CardConsultation() {
        serviceConsultation = new ServiceConsultation(); // Initialisation du service
    }

    // Méthode pour initialiser les données de la consultation
    public void initialize(Consultation consultation) {
        this.consultation = consultation;  // Ensure the consultation object is set
        if (consultation != null) {
            labelDate.setText("Date: " + consultation.getRendezVous().getDateHeure().toLocalDate()); // Affiche uniquement la date
            labelCommentaire.setText("Commentaire: " + consultation.getObservation());
            labelPrix.setText("Prix: " + consultation.getPrix());
            labelDuree.setText("Durée: " + consultation.getDuree());
            labelProchainRdv.setText("Prochain RDV: " + consultation.getProchainRdv());
        } else {
            // Handle the case where consultation is null
            labelDate.setText("Date: N/A");
            labelCommentaire.setText("Commentaire: N/A");
            labelPrix.setText("Prix: N/A");
            labelDuree.setText("Durée: N/A");
            labelProchainRdv.setText("Prochain RDV: N/A");
        }
    }

    @FXML
    private void supprimerConsultation() {
        if (consultation == null) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Erreur");
            error.setHeaderText(null);
            error.setContentText("Aucune consultation à supprimer.");
            error.showAndWait();
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Voulez-vous vraiment supprimer cette consultation ?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    serviceConsultation.supprimer(consultation.getId()); // Remplacer par le service approprié pour Consultation

                    Alert info = new Alert(Alert.AlertType.INFORMATION);
                    info.setTitle("Succès");
                    info.setHeaderText(null);
                    info.setContentText("Consultation supprimée avec succès !");
                    info.showAndWait();

                    // Supprimer proprement le nœud de l'affichage
                    if (rootPane.getParent() instanceof Pane parent) {
                        parent.getChildren().remove(rootPane);
                        parent.requestLayout(); // Force le recalcul du layout
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setTitle("Erreur");
                    error.setHeaderText(null);
                    error.setContentText("Erreur lors de la suppression de la consultation !");
                    error.showAndWait();
                }
            }
        });
    }
    @FXML
    private void modifierConsultation() {
        try {
            // Charger le fichier FXML pour ModifierConsultation
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierConsultation.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur associé au fichier FXML
            ModifierConsultation controller = loader.getController();

            // Passer la consultation à modifier au contrôleur
            controller.setConsultation(consultation); // Vous devez remplacer 'consultation' par l'objet de la consultation à modifier
            controller.setParentCard(this); // Passer la carte à rafraîchir (si nécessaire)

            // Créer une nouvelle fenêtre pour afficher la modification de la consultation
            Stage stage = new Stage();
            stage.setTitle("Modifier Consultation");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            // Afficher une alerte en cas d'erreur
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Impossible d'ouvrir le formulaire de modification de la consultation !");
            alert.showAndWait();
        }
    }


}
