package org.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.entities.Notification;
import org.example.entities.Planification;
import org.example.entities.Utilisateur;
import org.example.entities.UserConnecter;

import org.example.services.ServiceNotification;
import org.example.services.ServicesPlanification;
import org.example.services.ServiceUtilisateur;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class AddPlanificationController {

    @FXML private DatePicker dateField;
    @FXML private TextField adresseField;
    @FXML private ComboBox<String> modeComboBox;
    @FXML private ComboBox<Utilisateur> freelancerComboBox;
    public Utilisateur CurrentUser=UserConnecter.getInstance().getUserConnecter();

    private Runnable refreshCallback;
    private ServicesPlanification servicesPlanification = new ServicesPlanification();
    private ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();

    public void setRefreshCallback(Runnable callback) {
        this.refreshCallback = callback;
    }

    @FXML
    public void initialize() {
        modeComboBox.getItems().addAll("Présentiel", "À distance");
        try {
            List<Utilisateur> freelancers = serviceUtilisateur.findFreelancers();
            List<Utilisateur> users = serviceUtilisateur.afficher(); // Or findClients() if you have that method
            freelancerComboBox.getItems().addAll(freelancers);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSave() {
        try {
            // Validate Date
            LocalDate date = dateField.getValue();
            if (date == null) {
                showAlert("Validation Error", "Please select a date.");
                return;
            } else if (date.isBefore(LocalDate.now())) {
                showAlert("Validation Error", "The date cannot be in the past.");
                return;
            }

            // Validate Adresse
            String adresse = adresseField.getText().trim();
            if (adresse.isEmpty()) {
                showAlert("Validation Error", "Address field cannot be empty.");
                return;
            }

            // Validate Mode
            String mode = modeComboBox.getValue();
            if (mode == null || mode.isEmpty()) {
                showAlert("Validation Error", "Please select a mode (Présentiel or À distance).");
                return;
            }

            // Validate Freelancer
            Utilisateur freelancer = freelancerComboBox.getValue();
            if (freelancer == null) {
                showAlert("Validation Error", "Please select a freelancer.");
                return;
            }



            // If all inputs are valid, create and save the planification
            Planification planification = new Planification();
            planification.setDate(date);
            planification.setAdresse(adresse);
            planification.setMode(mode);
            planification.setStatut("en attente");
            planification.setFreelancer(freelancer);
            planification.setUtilisateur(CurrentUser);

            servicesPlanification.add(planification);
            Notification notification = new Notification();
            notification.setMessage("Nouvelle demande de planification de " + CurrentUser.getPrenom());
            notification.setIsRead(false);
            notification.setReceiver(freelancer);
            new ServiceNotification().add(notification);


            if (refreshCallback != null) {
                refreshCallback.run();
            }

            closeWindow();

        } catch (SQLException e) {
            showAlert("Database Error", "Failed to save planification: " + e.getMessage());
        }
    }


    @FXML
    private void handleCancel() {
        closeWindow();
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