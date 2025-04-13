package org.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.entities.Consultation;
import org.example.entities.RendezVous;
import org.example.services.ServiceConsultation;
import org.example.services.ServiceRendezVous;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;


public class ModifierConsultation implements Initializable{
    @FXML
    private TextField diagnosticField;

    @FXML
    private TextField traitementField;

    @FXML
    private TextArea observationArea;

    @FXML
    private TextField prixField;

    @FXML
    private DatePicker prochainRdvPicker;

    @FXML
    private TextField dureeField;

    @FXML
    private ComboBox<RendezVous> comboRendezVous;

    @FXML
    private Button btnModifierConsultation;

    private Consultation consultationAModifier;

    private final ServiceConsultation consultationService = new ServiceConsultation();
    private final ServiceRendezVous rendezVousService = new ServiceRendezVous();

    // Appelé depuis l’extérieur pour passer la consultation à modifier
    public void setConsultation(Consultation consultation) {
        this.consultationAModifier = consultation;
        remplirChampsAvecConsultation();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Charger tous les rendez-vous dans la ComboBox
        List<RendezVous> listeRendezVous = new ArrayList<>();
        try {
            listeRendezVous = rendezVousService.getAllRendezVous();
        } catch (SQLException e) {
            e.printStackTrace();
            // Optionnel : afficher une alerte à l'utilisateur
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur de base de données");
            alert.setContentText("Impossible de charger les rendez-vous.");
            alert.showAndWait();
        }
        comboRendezVous.getItems().addAll(listeRendezVous);
    }

    private void remplirChampsAvecConsultation() {
        if (consultationAModifier != null) {
            diagnosticField.setText(consultationAModifier.getDiagnostic());
            traitementField.setText(consultationAModifier.getTraitement());
            observationArea.setText(consultationAModifier.getObservation());
            prixField.setText(String.valueOf(consultationAModifier.getPrix()));
            prochainRdvPicker.setValue(consultationAModifier.getProchainRdv());
            dureeField.setText(consultationAModifier.getDuree());
            comboRendezVous.setValue(consultationAModifier.getRendezVous());
        }
    }

    @FXML
    private void modifierConsultation(ActionEvent event) {
        if (consultationAModifier != null) {
            consultationAModifier.setDiagnostic(diagnosticField.getText());
            consultationAModifier.setTraitement(traitementField.getText());
            consultationAModifier.setObservation(observationArea.getText());
            consultationAModifier.setPrix(prixField.getText());
            consultationAModifier.setProchainRdv(prochainRdvPicker.getValue());
            consultationAModifier.setDuree(dureeField.getText());
            consultationAModifier.setRendezVous(comboRendezVous.getValue());

            try {
                consultationService.modifier(consultationAModifier);
                // Optionnel : informer l'utilisateur du succès
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setHeaderText(null);
                alert.setContentText("Consultation modifiée avec succès !");
                alert.showAndWait();
            } catch (SQLException e) {
                e.printStackTrace();
                // Afficher une alerte d'erreur
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText("Erreur lors de la modification");
                alert.setContentText("Impossible de modifier la consultation.");
                alert.showAndWait();
            }

            // Affichage ou redirection après modification
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("La consultation a été modifiée avec succès !");
            alert.showAndWait();
        }
    }

    public void setParentCard(CardConsultation cardConsultation) {
    }

    public void modifierConsultation(javafx.event.ActionEvent actionEvent) {
        if (consultationAModifier != null) {
            consultationAModifier.setDiagnostic(diagnosticField.getText());
            consultationAModifier.setTraitement(traitementField.getText());
            consultationAModifier.setObservation(observationArea.getText());
            consultationAModifier.setPrix(prixField.getText());
            consultationAModifier.setProchainRdv(prochainRdvPicker.getValue());
            consultationAModifier.setDuree(dureeField.getText());
            consultationAModifier.setRendezVous(comboRendezVous.getValue());

            try {
                consultationService.modifier(consultationAModifier);
                // Optionnel : informer l'utilisateur du succès
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setHeaderText(null);
                alert.setContentText("Consultation modifiée avec succès !");
                alert.showAndWait();
            } catch (SQLException e) {
                e.printStackTrace();
                // Afficher une alerte d'erreur
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText("Erreur lors de la modification");
                alert.setContentText("Impossible de modifier la consultation.");
                alert.showAndWait();
            }

            // Affichage ou redirection après modification
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("La consultation a été modifiée avec succès !");
            alert.showAndWait();
        }
    }
}
