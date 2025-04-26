package org.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.entities.Consultation;
import org.example.entities.RendezVous;
import org.example.services.ServiceConsultation;
import org.example.services.ServiceRendezVous;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class AjouterConsultation {
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
    private Button btnAjouterConsultation;

    private final ServiceConsultation serviceConsultation = new ServiceConsultation();
    private final ServiceRendezVous serviceRendezVous = new ServiceRendezVous();

    @FXML
    public void initialize() {
        try {
            List<RendezVous> rendezVousList = serviceRendezVous.afficher();
            comboRendezVous.getItems().addAll(rendezVousList);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur lors du chargement des rendez-vous : " + e.getMessage());
            e.printStackTrace(); // utile pour debug
        }
        prochainRdvPicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.isBefore(LocalDate.now().plusDays(1))) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;"); // facultatif : couleur rouge clair
                }
            }
        });
    }


    @FXML
    private void ajouterConsultation() throws SQLException {
        String diagnostic = diagnosticField.getText();
        String traitement = traitementField.getText();
        String observation = observationArea.getText();
        String prixText = prixField.getText();
        LocalDate prochainRdv = prochainRdvPicker.getValue();
        String duree = dureeField.getText();
        RendezVous selectedRdv = comboRendezVous.getValue();

        // Validation de base
        if (diagnostic.isEmpty() || traitement.isEmpty() || prixText.isEmpty() || duree.isEmpty() || selectedRdv == null) {
            showAlert(Alert.AlertType.ERROR, "Veuillez remplir tous les champs obligatoires.");
            return;
        }


        try {
            Double.parseDouble(prixText); // Juste pour vérifier la validité
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Le prix doit être un nombre valide.");
            return;
        }
        try {
            int dureeInt = Integer.parseInt(duree);
            if (dureeInt <= 0) {
                showAlert(Alert.AlertType.ERROR, "La durée doit être un nombre entier strictement positif.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "La durée doit être un nombre entier valide.");
            return;
        }



        Consultation consultation = new Consultation();
        consultation.setDiagnostic(diagnostic);
        consultation.setTraitement(traitement);
        consultation.setObservation(observation);
        consultation.setPrix(prixText);
        consultation.setProchainRdv(prochainRdv);
        consultation.setDuree(duree);
        consultation.setRendezVous(selectedRdv);

        serviceConsultation.ajouter(consultation);
        showAlert(Alert.AlertType.INFORMATION, "Consultation ajoutée avec succès !");
        clearFields();
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.show();
    }

    private void clearFields() {
        diagnosticField.clear();
        traitementField.clear();
        observationArea.clear();
        prixField.clear();
        prochainRdvPicker.setValue(null);
        dureeField.clear();
        comboRendezVous.setValue(null);
    }
}
