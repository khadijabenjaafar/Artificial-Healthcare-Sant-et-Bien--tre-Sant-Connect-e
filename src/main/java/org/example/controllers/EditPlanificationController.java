
package org.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.entities.Planification;
import org.example.entities.Utilisateur;
import org.example.services.ServicesPlanification;
import org.example.services.ServiceUtilisateur;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class EditPlanificationController {

    @FXML private DatePicker dateField;
    @FXML private TextField adresseField;
    @FXML private ComboBox<String> modeComboBox;
    @FXML private ComboBox<Utilisateur> freelancerComboBox;
    @FXML private ComboBox<Utilisateur> utilisateurComboBox;
    @FXML private ComboBox<String> statutComboBox;
    @FXML private TextField reponseField;

    private Planification planification;
    private Runnable refreshCallback;
    private ServicesPlanification servicesPlanification = new ServicesPlanification();
    private ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();

    public void setPlanification(Planification planification) {
        this.planification = planification;
        populateFields();
    }

    public void setRefreshCallback(Runnable callback) {
        this.refreshCallback = callback;
    }

    @FXML
    public void initialize() {
        // Initialize combo boxes
        modeComboBox.getItems().addAll("Présentiel", "À distance");
        statutComboBox.getItems().addAll("en attente", "confirmée", "annulée");

        try {
            // Load freelancers and users
            List<Utilisateur> freelancers = serviceUtilisateur.findFreelancers();
            List<Utilisateur> users = serviceUtilisateur.afficher();

            freelancerComboBox.getItems().addAll(freelancers);
            utilisateurComboBox.getItems().addAll(users);
        } catch (SQLException e) {
            showAlert("Database Error", "Error loading users: " + e.getMessage());
        }
    }

    private void populateFields() {
        if (planification != null) {
            dateField.setValue(planification.getDate());
            adresseField.setText(planification.getAdresse());
            modeComboBox.setValue(planification.getMode());
            statutComboBox.setValue(planification.getStatut());
            reponseField.setText(planification.getReponse());

            // Set freelancer and user if they exist
            if (planification.getFreelancer() != null) {
                freelancerComboBox.getItems().stream()
                        .filter(f -> f.getId() == planification.getFreelancer().getId())
                        .findFirst()
                        .ifPresent(freelancerComboBox::setValue);
            }

            if (planification.getUtilisateur() != null) {
                utilisateurComboBox.getItems().stream()
                        .filter(u -> u.getId() == planification.getUtilisateur().getId())
                        .findFirst()
                        .ifPresent(utilisateurComboBox::setValue);
            }
        }
    }

    @FXML
    private void handleSave() {
        if (validateFields()) {
            try {
                // Update the planification object
                planification.setDate(dateField.getValue());
                planification.setAdresse(adresseField.getText());
                planification.setMode(modeComboBox.getValue());
                planification.setStatut(statutComboBox.getValue());
                planification.setReponse(reponseField.getText());
                planification.setFreelancer(freelancerComboBox.getValue());
                planification.setUtilisateur(utilisateurComboBox.getValue());

                if (planification.getId() == null) {
                    showAlert("Internal Error", "Impossible de modifier une planification sans ID.");
                    return;
                }

                servicesPlanification.update(planification);

                if (refreshCallback != null) {
                    refreshCallback.run();
                }

                closeWindow();
            } catch (SQLException e) {
                showAlert("Database Error", "Failed to update planification: " + e.getMessage());
            }
        }
    }


    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private boolean validateFields() {
        StringBuilder errors = new StringBuilder();

        LocalDate date = dateField.getValue();
        if (date == null) {
            errors.append("• Date is required.\n");
        } else if (date.isBefore(LocalDate.now())) {
            errors.append("• Date cannot be in the past.\n");
        }

        if (adresseField.getText() == null || adresseField.getText().trim().isEmpty()) {
            errors.append("• Address is required.\n");
        }

        if (modeComboBox.getValue() == null || modeComboBox.getValue().trim().isEmpty()) {
            errors.append("• Mode is required.\n");
        }

        if (statutComboBox.getValue() == null || statutComboBox.getValue().trim().isEmpty()) {
            errors.append("• Status is required.\n");
        }

        if (freelancerComboBox.getValue() == null) {
            errors.append("• Freelancer is required.\n");
        }

        if (utilisateurComboBox.getValue() == null) {
            errors.append("• User is required.\n");
        }

        if (reponseField.getText() != null && reponseField.getText().length() > 255) {
            errors.append("• Response cannot exceed 255 characters.\n");
        }

        if (errors.length() > 0) {
            showAlert("Validation Error", "Please fix the following errors:\n\n" + errors.toString());
            return false;
        }

        return true;
    }


    private void closeWindow() {
        Stage stage = (Stage) dateField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

}