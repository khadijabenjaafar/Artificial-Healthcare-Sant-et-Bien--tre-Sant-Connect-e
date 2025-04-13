package org.example.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.example.entities.RendezVous;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.scene.control.Label;
import org.example.services.ServiceRendezVous;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

public class CardRendezVous {
    @FXML
    private Label labelDate;
    @FXML
    private Label labelMotif;
    @FXML
    private Label labelStatut;
    @FXML
    private Label labelMode;
    @FXML
    private Label labelMedecin;
    @FXML
    private Label labelPatient;
    @FXML
    private Label labelCommentaire;
    @FXML
    private Button btnSupprimer;
    @FXML
    private Button btnModifier;
    @FXML
    private AnchorPane rootPane; // pour éventuellement supprimer visuellement la carte

    private RendezVous rendezVous; // pour stocker l'objet courant
    private final ServiceRendezVous service = new ServiceRendezVous();

    public void setData(RendezVous rv) {
        this.rendezVous = rv;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        labelDate.setText("Date : " + rv.getDateHeure().format(formatter));
        labelMotif.setText(" Motif : " + rv.getMotif());
        labelStatut.setText(" Statut : " + rv.getStatut());
        labelMode.setText(" Mode : " + rv.getMode());

        if (rv.getMedecin() != null)
            labelMedecin.setText("Médecin : " + rv.getMedecin().getNom());

        if (rv.getPatient() != null)
            labelPatient.setText(" Patient : " + rv.getPatient().getNom());

        labelCommentaire.setText(" Commentaire : " + rv.getCommentaire());
        btnSupprimer.setOnAction(e -> supprimerRendezVous());
        btnModifier.setOnAction(e -> modifierRendezVous());

    }

    private void supprimerRendezVous() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Voulez-vous vraiment supprimer ce rendez-vous ?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    service.supprimer(rendezVous.getId());

                    Alert info = new Alert(Alert.AlertType.INFORMATION);
                    info.setTitle("Succès");
                    info.setHeaderText(null);
                    info.setContentText("Rendez-vous supprimé avec succès !");
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
                    error.setContentText("Erreur lors de la suppression !");
                    error.showAndWait();
                }
            }
        });
    }
    private void modifierRendezVous() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierRendezVous.fxml"));
            Parent root = loader.load();

            ModifierRendezVous controller = loader.getController();
            controller.setRendezVous(rendezVous); // Donne-lui le rendez-vous à modifier
            controller.setParentCard(this);       // Donne-lui la carte à rafraîchir

            Stage stage = new Stage();
            stage.setTitle("Modifier Rendez-vous");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Impossible d'ouvrir le formulaire de modification !");
            alert.showAndWait();
        }
    }

    public void refreshData() {
        setData(this.rendezVous); // réutilise setData pour re-remplir les champs avec les données mises à jour
    }
    public void setHighlighted(boolean highlight) {
        if (highlight) {
            rootPane.getStyleClass().add("highlighted-card");

            // Supprimer la coloration après 2 secondes
            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(e -> rootPane.getStyleClass().remove("highlighted-card"));
            pause.play();
        } else {
            rootPane.getStyleClass().remove("highlighted-card");
        }
    }





}
