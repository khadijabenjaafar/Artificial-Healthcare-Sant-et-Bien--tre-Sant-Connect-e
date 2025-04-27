package org.example.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.example.api.mailler;
import org.example.entities.Utilisateur;
import org.example.services.ServiceUtilisateur;

import java.io.IOException;

public class ForgetPassword {
    @FXML
    private TextField Email;

    @FXML
    private Button oublier;

    @FXML
    private Label navsignup;
    private ServiceUtilisateur userService = new ServiceUtilisateur();
    private Utilisateur user;
    @FXML
    public void initialize() {
        // Set an action event handler for the Send button
        oublier.setOnAction(event -> Submit());
    }
    @FXML
    void Submit() {
        String emailAddress = Email.getText();

        if (!emailAddress.isEmpty()) {
            // Create a User object (assuming User class has a constructor that accepts email)
            user = userService.getUserByEmail(emailAddress);
            if (user != null) {
                try {
                    // Send the email using the mailer API
                    mailler.sendResetPasswordEmail(user,userService);

                    // Show a success message to the user
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Email sent successfully to: " + emailAddress);

                    // Navigate to /Reinitialiser.fxml
                    navigateToReinitialiser();

                } catch (Exception e) {
                    // Show an error message if sending the email fails
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to send email: " + e.getMessage());
                }
            } else {
                // Show an error message if the email does not exist
                showAlert(Alert.AlertType.ERROR, "Error", "Email does not exist.");
            }
        } else {
            // Show an error message if the email field is empty
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter an email address.");
        }
    }




    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void navigateToReinitialiser() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Reinitialiser.fxml"));
            Parent root = loader.load();

// Obtiens le contrôleur et passe-lui l’utilisateur
            Reinitialiser controller = loader.getController();
            controller.setUtilisateur(user); // <-- utilisateur est l'objet que tu veux transmettre

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            // Show an error message if navigation fails
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to navigate to login page: " + e.getMessage());
        }
    }
    @FXML
    public void navigateToSignUp(MouseEvent mouseEvent) {
        try {
            // Load the SignUpView.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CreerCompte.fxml"));
            Parent root = loader.load();

            // Get the current stage (window)
            Stage stage = (Stage) navsignup.getScene().getWindow();

            // Set the new scene with the SignUpView.fxml content
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Unable to load the sign-up screen. Please check the file path.");
        } catch (NullPointerException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "The SignUpView.fxml file was not found. Please check the file path.");
        }
    }
}
