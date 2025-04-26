package org.example.controllers;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.example.entities.Utilisateur;
import org.example.services.ServiceUtilisateur;
import  org.example.entities.UserConnecter;

public class Login {
    @FXML
    private TextField Email;

    @FXML
    private Button loginBtn;

    @FXML
    private Label navsignup;

    @FXML
    private PasswordField password;



    @FXML
    void Submit(ActionEvent event) {
        // Retrieve email and password from the input fields
        String email = Email.getText();
        String password1 = password.getText();

        // Validate inputs
        if (email.isEmpty() || password1.isEmpty()) {
            showAlert(AlertType.ERROR, "Input Error", "Please fill in all fields.");
            return;
        }

        // Authenticate the user
        Utilisateur user = ServiceUtilisateur.getUserByEmailAndPass(email, password1);
        if (user != null) {
            // Authentication successful
            showAlert(AlertType.INFORMATION, "Login Successful", "Welcome, " + user.getNom() + "!");

            UserConnecter.getInstance().setUserConnecter(user);
            String role =user.getRole().toString();
            if(role.equals("ROLE_ADMIN")){
                navigateToBackAdmin();
            } else if (role.equals("ROLE_FREELANCER")||role.equals("ROLE_PHARMACIEN")||role.equals("ROLE_MEDECIN")) {
                navigateToDoctor();
            }
            else{
                navigateToHome();
            }
        } else {
            // Authentication failed
            showAlert(AlertType.ERROR, "Login Failed", "Invalid email or password.");
        }
    }

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void navigateToHome() {
        try {
            // Load the home.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/indexFront.fxml"));
            Parent root = loader.load();

            // Get the current stage (window)
            Stage stage = (Stage) loginBtn.getScene().getWindow();

            // Set the new scene with the home.fxml content
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Navigation Error", "Unable to load the home screen.");
        }
    }
    private void navigateToDoctor() {
        try {
            // Load the home.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/doctor.fxml"));
            Parent root = loader.load();

            // Get the current stage (window)
            Stage stage = (Stage) loginBtn.getScene().getWindow();

            // Set the new scene with the home.fxml content
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Navigation Error", "Unable to load the home screen.");
        }
    }

    private void navigateToBackAdmin() {
        try {
            // Load the home.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/BackAdmin.fxml"));
            Parent root = loader.load();

            // Get the current stage (window)
            Stage stage = (Stage) loginBtn.getScene().getWindow();

            // Set the new scene with the home.fxml content
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Navigation Error", "Unable to load the home screen.");
        }
    }

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
            showAlert(AlertType.ERROR, "Navigation Error", "Unable to load the sign-up screen. Please check the file path.");
        } catch (NullPointerException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Navigation Error", "The SignUpView.fxml file was not found. Please check the file path.");
        }
    }
}
