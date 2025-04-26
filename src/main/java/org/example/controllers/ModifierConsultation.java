package org.example.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.entities.Consultation;
import org.example.services.ServiceConsultation;

import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ModifierConsultation implements Initializable {

    @FXML private TextField observationField;
    @FXML private TextField prixField;
    @FXML private TextField dureeField;
    @FXML private DatePicker prochainRdvPicker;
    @FXML private TextArea diagnosticArea;
    @FXML private TextArea traitementArea;

    private Consultation consultation;
    private final ServiceConsultation service = new ServiceConsultation();
    private CardConsultation parentController;

    public void setParentController(CardConsultation controller) {
        this.parentController = controller;
    }

    public void setConsultation(Consultation consultation) {
        this.consultation = consultation;
        remplirChamps();
    }

    private void remplirChamps() {
        if (consultation != null) {
            observationField.setText(consultation.getObservation());
            prixField.setText(String.valueOf(consultation.getPrix()));
            dureeField.setText(consultation.getDuree());
            if (consultation.getProchainRdv() != null)
                prochainRdvPicker.setValue(consultation.getProchainRdv());

            diagnosticArea.setText(consultation.getDiagnostic());
            traitementArea.setText(consultation.getTraitement());
        }
    }

    @FXML
    private void handleSave() {
        try {
            consultation.setObservation(observationField.getText());
            consultation.setPrix(prixField.getText());
            consultation.setDuree(dureeField.getText());
            consultation.setProchainRdv(prochainRdvPicker.getValue());

            consultation.setDiagnostic(diagnosticArea.getText());
            consultation.setTraitement(traitementArea.getText());

            service.modifier(consultation);
            // Appel de la méthode de rafraîchissement
            if (parentController != null) {
                parentController.rafraichirAffichage();
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Consultation modifiée avec succès !");
            alert.showAndWait();

            ((Stage) observationField.getScene().getWindow()).close();

        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Erreur");
            error.setHeaderText(null);
            error.setContentText("Erreur lors de la modification !");
            error.showAndWait();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // rien à initialiser ici
    }
}
